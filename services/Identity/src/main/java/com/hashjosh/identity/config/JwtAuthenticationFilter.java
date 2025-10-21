package com.hashjosh.identity.config;

import com.hashjosh.identity.entity.User;
import com.hashjosh.identity.properties.JwtProperties;
import com.hashjosh.identity.repository.UserRepository;
import com.hashjosh.identity.service.JwtService;
import com.hashjosh.identity.service.RefreshTokenService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final TrustedConfig trustedConfig;
    private final RefreshTokenService refreshTokenService;
    private final UserRepository userRepository;
    private final JwtProperties jwtProperties;

    private static final String INTERNAL_SERVICE_HEADER = "X-Internal-Service";
    private static final String USER_ID_HEADER = "X-User-Id";
    private static final String REFRESH_TOKEN_HEADER = "X-Refresh-Token";
    private static final String USER_AGENT_HEADER = "User-Agent";

    private static final Set<String> PUBLIC_ENDPOINTS = Set.of(
            "/api/v1/users/auth/login",
            "/api/v1/users/auth/registration"
    );

    private static final Set<String> WEBSOCKET_PATTERNS = Set.of(
            "/ws",
            "/ws/",
            "/ws/info"
    );

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        log.debug("📝 Checking path for filtering: {}", path);

        if (PUBLIC_ENDPOINTS.contains(path)) {
            log.debug("🔓 Bypassing filter for public endpoint: {}", path);
            return true;
        }

        boolean isWebSocketPath = WEBSOCKET_PATTERNS.stream()
                .anyMatch(path::startsWith);

        if (isWebSocketPath) {
            log.debug("🔓 Bypassing filter for WebSocket path: {}", path);
            return true;
        }

        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String requestUri = request.getRequestURI();

        if (handleInternalService(request, response, filterChain)) {
            return;
        }

        try {
            processAuthentication(request, response, filterChain);
        } catch (Exception e) {
            handleAuthenticationError(response, requestUri, e);
        }
    }

    private boolean handleInternalService(HttpServletRequest request,
                                        HttpServletResponse response,
                                        FilterChain filterChain) throws IOException, ServletException {
        String internalServiceHeader = request.getHeader(INTERNAL_SERVICE_HEADER);
        if (internalServiceHeader == null) {
            return false;
        }

        log.debug("X-Internal-Service header: {}, Trusted service IDs: {}", 
                 internalServiceHeader, trustedConfig.getInternalServiceIds());

        if (trustedConfig.getInternalServiceIds().contains(internalServiceHeader)) {
            authenticateInternalService(request, internalServiceHeader);
            try {
                filterChain.doFilter(request, response);
                return true;
            } catch (Exception e) {
                handleInternalServiceError(response, request.getRequestURI(), e);
                return true;
            }
        } else {
            sendUnauthorizedResponse(response, "Invalid X-Internal-Service header");
            return true;
        }
    }

    private void authenticateInternalService(HttpServletRequest request, String serviceId) {
        String userIdStr = extractUserIdFromHeader(request);
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        new CustomUserDetails(serviceId, UUID.fromString(userIdStr), 
                            Set.of(new SimpleGrantedAuthority("ROLE_INTERNAL_SERVICE"))),
                        null,
                        Set.of(new SimpleGrantedAuthority("ROLE_INTERNAL_SERVICE"))
                );
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.info("Internal service request authenticated: {}", serviceId);
    }

    private void processAuthentication(HttpServletRequest request,
                                     HttpServletResponse response,
                                     FilterChain filterChain) 
            throws IOException, ServletException {
        String requestUri = request.getRequestURI();
        String accessToken = extractAccessToken(request);
        String refreshToken = extractRefreshToken(request);
        String clientIp = request.getRemoteAddr();
        String userAgent = request.getHeader(USER_AGENT_HEADER);

        log.debug("[JWT Filter] URI: {} | Access token present: {} | Refresh token present: {}",
                requestUri, accessToken != null, refreshToken != null);

        if (accessToken == null) {
            log.warn("Unauthorized request to {}: Missing Authorization header", requestUri);
            sendUnauthorizedResponse(response, "Missing Authorization header");
            return;
        }

        try {
            if (!jwtService.isExpired(accessToken) && jwtService.validateToken(accessToken)) {
                log.info("[JWT Filter] Valid access token for URI: {}", requestUri);
                setAuthentication(accessToken, request);
                filterChain.doFilter(request, response);
                return;
            }

            if (refreshToken != null && refreshTokenService.validateRefreshToken(refreshToken)) {
                log.info("[JWT Filter] Expired access token, valid refresh token for URI: {}", requestUri);
                Claims claim = jwtService.getAllClaims(accessToken);
                String userId = claim.get("userId", String.class);

                User user = userRepository.findById(UUID.fromString(userId))
                        .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

                String newAccessToken = jwtService.generateAuthToken(user, jwtProperties.getAccessTokenExpirationMs());
                String newRefreshToken = jwtService.generateSecureRefreshToken();

                refreshTokenService.deleteByToken(refreshToken);
                refreshTokenService.saveRefreshToken(user, newRefreshToken, clientIp, userAgent);

                log.info("[JWT Filter] Token refresh successful for user ID: {}", userId);

                setAuthentication(newAccessToken, request);
                addTokenCookies(response, newAccessToken, newRefreshToken);
                filterChain.doFilter(request, response);
                return;
            }

            log.warn("[JWT Filter] Invalid or expired token for URI: {}", requestUri);
            sendUnauthorizedResponse(response, "Invalid or expired token");
        } catch (Exception e) {
            handleAuthenticationError(response, requestUri, e);
        }
    }

    private void setAuthentication(String accessToken, HttpServletRequest request) {
        Claims claims = jwtService.getAllClaims(accessToken);

        Set<SimpleGrantedAuthority> authorities = new HashSet<>();

        if (claims.get("roles") instanceof Collection<?>) {
            ((Collection<?>) claims.get("roles")).forEach(role -> {
                authorities.add(new SimpleGrantedAuthority("ROLE_" + role.toString().toUpperCase()));
            });
        }

        if (claims.get("permissions") instanceof Collection<?>) {
            ((Collection<?>) claims.get("permissions")).forEach(permission -> {
                authorities.add(new SimpleGrantedAuthority(permission.toString().toUpperCase()));
            });
        }

        log.debug("[JWT Filter] Extracted authorities from token: {}", authorities);

        CustomUserDetails userDetails = new CustomUserDetails(claims, authorities);

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                authorities
        );

        auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(auth);

        log.debug("[JWT Filter] Successfully set authentication for user: {} with authorities: {}",
                userDetails.getUsername(), authorities);
    }

    private void addTokenCookies(HttpServletResponse response, String accessToken, String refreshToken) {
        var accessCookie = new jakarta.servlet.http.Cookie("ACCESS_TOKEN", accessToken);
        accessCookie.setHttpOnly(true);
        accessCookie.setPath("/");
        response.addCookie(accessCookie);

        var refreshCookie = new jakarta.servlet.http.Cookie("REFRESH_TOKEN", refreshToken);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setPath("/");
        response.addCookie(refreshCookie);
    }

    private String extractAccessToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private String extractRefreshToken(HttpServletRequest request) {
        String headerToken = request.getHeader("X-Refresh-Token");
        return (headerToken != null && !headerToken.isEmpty()) ? headerToken : null;
    }


    private void handleAuthenticationError(HttpServletResponse response, 
                                         String requestUri, 
                                         Exception e) throws IOException {
        log.error("[JWT Filter] Authentication error for URI: {} | Exception: {}", 
                 requestUri, e.getMessage(), e);
        sendErrorResponse(response, 
                HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                "Authentication error: " + e.getMessage());
    }

    private void handleInternalServiceError(HttpServletResponse response, 
                                          String requestUri, 
                                          Exception e) throws IOException {
        log.error("Error processing internal service request {}: {}", 
                 requestUri, e.getMessage(), e);
        sendErrorResponse(response, 
                HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                "Internal server error: " + e.getMessage());
    }

    private void sendUnauthorizedResponse(HttpServletResponse response, 
                                        String message) throws IOException {
        sendErrorResponse(response, 
                HttpServletResponse.SC_UNAUTHORIZED, 
                "Unauthorized - " + message);
    }

    private void sendErrorResponse(HttpServletResponse response, 
                                 int status, 
                                 String message) throws IOException {
        if (!response.isCommitted()) {
            response.setStatus(status);
            response.setContentType("application/json");
            response.getWriter().write(String.format(
                    "{\"message\": \"%s\"}", message));
            response.getWriter().flush();
        }
    }

    private String extractUserIdFromHeader(HttpServletRequest request) {
        String userIdHeader = request.getHeader(USER_ID_HEADER);
        return (userIdHeader != null && !userIdHeader.isEmpty()) ? userIdHeader : null;
    }
}
