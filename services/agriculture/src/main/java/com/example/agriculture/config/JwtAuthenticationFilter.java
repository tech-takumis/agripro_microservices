package com.example.agriculture.config;

import com.example.agriculture.service.TokenRenewalService;
import com.hashjosh.jwtshareable.service.JwtService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
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
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final TokenRenewalService tokenRenewalService;
    private final TrustedConfig trustedConfig;

    private static final String INTERNAL_SERVICE_HEADER = "X-Internal-Service";
    private static final String USERID_HEADER = "X-User-Id";
    private static final Set<String> PUBLIC_ENDPOINTS = Set.of(
            "/api/v1/agriculture/auth/login",
            "/api/v1/agriculture/auth/registration"
    );
    private static final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String requestUri = request.getRequestURI();
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        // Allow pattern matching for public endpoints
        return PUBLIC_ENDPOINTS.stream().anyMatch(p -> pathMatcher.match(p, requestUri));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String requestUri = request.getRequestURI();
        log.debug("Processing request: {} with headers: {}", requestUri, getHeadersAsString(request));

        // Check for internal service header
        String internalServiceHeader = request.getHeader(INTERNAL_SERVICE_HEADER);
        String userIdHeader = request.getHeader(USERID_HEADER);
        log.debug("X-Internal-Service header: {}, Trusted service IDs: {}", internalServiceHeader, trustedConfig.getInternalServiceIds());
        if ((internalServiceHeader != null && !internalServiceHeader.isEmpty()) || (userIdHeader != null && !userIdHeader.isEmpty())) {
            if (trustedConfig.getInternalServiceIds().contains(internalServiceHeader)) {
                log.info("Internal service request from {} to {}", internalServiceHeader, requestUri);
                UsernamePasswordAuthenticationToken authentication = getUsernamePasswordAuthenticationToken(internalServiceHeader, userIdHeader);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
                filterChain.doFilter(request, response);
                return;
            } else {
                log.warn("Invalid X-Internal-Service header: {}. Expected one of: {}", internalServiceHeader, trustedConfig.getInternalServiceIds());
                sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized - Invalid X-Internal-Service header");
                return;
            }
        }

        // Extract tokens
        String accessToken = extractAccessToken(request);
        String refreshToken = extractRefreshToken(request);
        String clientIp = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");

        // Reject requests without access token or internal service header
        if (accessToken == null) {
            log.warn("Unauthorized request to {}: Missing both X-Internal-Service and Authorization headers", requestUri);
            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized - Missing X-Internal-Service or Authorization header");
            return;
        }

        // Validate access token
        try {
            if (!jwtService.isExpired(accessToken) && jwtService.validateToken(accessToken)) {
                setAuthentication(accessToken, request);
                filterChain.doFilter(request, response);
                return;
            }
            // Handle expired access token with valid refresh token
            if (refreshToken != null) {
                Claims claims = jwtService.getClaimsAllowExpired(accessToken);
                String username = claims.getSubject();
                String userId = claims.get("userId", String.class);
                Map<String, Object> claimsMap = new HashMap<>();
                claimsMap.put("userId", userId);
                Map<String, String> newTokens = tokenRenewalService.refreshTokens(
                        UUID.fromString(userId), refreshToken, username,
                         claimsMap, clientIp, userAgent, false);
                setAuthentication(newTokens.get("accessToken"), request);
                addTokenCookies(response, newTokens.get("accessToken"), newTokens.get("refreshToken"));
                filterChain.doFilter(request, response);
                return;
            }
            // Reject invalid or expired tokens
            log.warn("Unauthorized request to {}: Invalid or expired token", requestUri);
            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized - Invalid or expired token");
        } catch (Exception e) {
            log.error("Authentication error for request {}: {}", requestUri, e.getMessage(), e);
            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized - Authentication error: " + e.getMessage());
        }
    }

    private static UsernamePasswordAuthenticationToken getUsernamePasswordAuthenticationToken(String internalServiceHeader, String userIdHeader) {
        CustomUserDetails userDetails = new CustomUserDetails(
                internalServiceHeader, userIdHeader,
                Set.of(new SimpleGrantedAuthority("ROLE_INTERNAL_SERVICE"))
        );
        return new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                Set.of(new SimpleGrantedAuthority("ROLE_INTERNAL_SERVICE"))
        );
    }

    private void setAuthentication(String accessToken, HttpServletRequest request) {
        Claims claims = jwtService.getAllClaims(accessToken);

        Set<SimpleGrantedAuthority> authorities = new HashSet<>();

        // Add roles
        if (claims.get("roles") instanceof Collection<?>) {
            ((Collection<?>) claims.get("roles")).forEach(role -> {
                authorities.add(new SimpleGrantedAuthority("ROLE_" + role.toString().toUpperCase()));
            });
        }

        // Add permissions
        if (claims.get("permissions") instanceof Collection<?>) {
            ((Collection<?>) claims.get("permissions")).forEach(permission -> {
                authorities.add(new SimpleGrantedAuthority(permission.toString().toUpperCase()));
            });
        }

        log.debug("[JWT Filter] Extracted authorities from token: {}", authorities);

        // Create CustomUserDetails directly from claims
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

        Cookie[] cookies = request.getCookies();
        if(cookies != null){
            for(Cookie cookie: cookies){
                if("ACCESS_TOKEN".equals(cookie.getName())){
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    private String extractRefreshToken(HttpServletRequest request) {
        String headerToken = request.getHeader("X-Refresh-Token");
        return (headerToken != null && !headerToken.isEmpty()) ? headerToken : null;
    }

    private String getHeadersAsString(HttpServletRequest request) {
        StringBuilder headers = new StringBuilder();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            headers.append(headerName).append(": ").append(request.getHeader(headerName)).append("; ");
        }
        return headers.toString();
    }

    private void sendErrorResponse(HttpServletResponse response, int status, String message) throws IOException {
        if (!response.isCommitted()) {
            response.sendError(status, message);
            response.setContentType("application/json");
            response.getWriter().write(String.format("{\"message\": \"%s\"}", message));
            response.getWriter().flush();
        }
    }
}
