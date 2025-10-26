package com.hashjosh.pcic.config;


import com.hashjosh.jwtshareable.service.JwtService;
import com.hashjosh.pcic.entity.Pcic;
import com.hashjosh.pcic.repository.PcicRepository;
import com.hashjosh.pcic.service.TokenRenewalService;
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
import org.springframework.security.core.Authentication;
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
    private final TokenRenewalService tokenRenewalService;
    private final TrustedConfig trustedConfig;
    private final PcicRepository pcicRepository;

    private static final String INTERNAL_SERVICE_HEADER = "X-Internal-Service";
    private static final String USERID_HEADER = "X-User-Id";
    private static final Set<String> PUBLIC_ENDPOINTS = Set.of(
            "/api/v1/pcic/auth/login",
            "/api/v1/pcic/auth/registration"
    );

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String requestUri = request.getRequestURI();
        return PUBLIC_ENDPOINTS.contains(requestUri);
    }

    private boolean handleInternalServiceAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        String internalServiceHeader = request.getHeader(INTERNAL_SERVICE_HEADER);
        String userIdHeader = request.getHeader(USERID_HEADER);
        String requestUri = request.getRequestURI();
        log.debug("X-Internal-Service header: {}, Trusted service IDs: {}", internalServiceHeader, trustedConfig.getInternalServiceIds());
        if (internalServiceHeader != null && !internalServiceHeader.isEmpty()) {
            if (trustedConfig.getInternalServiceIds().contains(internalServiceHeader)) {
                log.info("Internal service request from {} to {}", internalServiceHeader, requestUri);
                Set<SimpleGrantedAuthority> authorities = Set.of(new SimpleGrantedAuthority("ROLE_INTERNAL_SERVICE"));
                CustomUserDetails userDetails = new CustomUserDetails(
                        internalServiceHeader + UUID.randomUUID().toString().substring(0, 8),
                        UUID.fromString(userIdHeader),
                        authorities
                );
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                authorities
                        );
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
                try {
                    filterChain.doFilter(request, response);
                } catch (Exception e) {
                    log.error("Error processing internal service request {}: {}", requestUri, e.getMessage(), e);
                    if (!response.isCommitted()) {
                        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error");
                        response.setContentType("application/json");
                        response.getWriter().write(
                                "{\"message\": \"Internal server error: " + e.getMessage() + "\"}"
                        );
                        response.getWriter().flush();
                    }
                }
                return true;
            } else {
                log.warn("Invalid X-Internal-Service header: {}. Expected one of: {}", internalServiceHeader, trustedConfig.getInternalServiceIds());
                if (!response.isCommitted()) {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid X-Internal-Service header");
                    response.setContentType("application/json");
                    response.getWriter().write(
                            "{\"message\": \"Unauthorized - Invalid X-Internal-Service header and user id header\"}"
                    );
                    response.getWriter().flush();
                }
                return true;
            }
        }
        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        if (handleInternalServiceAuthentication(request, response, filterChain)) {
            return;
        }

        // Extract tokens
        String accessToken = extractAccessToken(request);
        String refreshToken = extractRefreshToken(request);
        String clientIp = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");

        // Reject requests without access token or internal service header
        if (accessToken == null) {
            log.warn("Unauthorized request to {}: Missing both X-Internal-Service and Authorization headers", request.getRequestURI());
            if (!response.isCommitted()) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing X-Internal-Service or Authorization header");
                response.setContentType("application/json");
                response.getWriter().write(
                        "{\"message\": \"Unauthorized - Missing X-Internal-Service or Authorization header\"}"
                );
                response.getWriter().flush();
            }
            return;
        }

        // Validate access token
        try {
            if (!jwtService.isExpired(accessToken) && jwtService.validateToken(accessToken)) {
                setAuthentication(accessToken);
                filterChain.doFilter(request, response);
                return;
            }

            // Handle expired access token with valid refresh token
            if (refreshToken != null ) {
                Claims claims = jwtService.getClaimsAllowExpired(accessToken);
                String username = claims.getSubject();
                String userId = claims.get("userId", String.class);

                Map<String, Object> claimsMap = new HashMap<>();
                claimsMap.put("userId", userId);

                Map<String, String> newTokens = tokenRenewalService.refreshTokens(
                        UUID.fromString(userId), refreshToken, username,
                        claimsMap, clientIp, userAgent, false);

                setAuthentication(newTokens.get("accessToken"));
                addTokenCookies(response, newTokens.get("accessToken"), newTokens.get("refreshToken"));
                filterChain.doFilter(request, response);
                return;
            }

            // Reject invalid or expired tokens
            log.warn("Unauthorized request to {}: Invalid or expired token", request.getRequestURI());
            if (!response.isCommitted()) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired token");
                response.setContentType("application/json");
                response.getWriter().write(
                        "{\"message\": \"Unauthorized - Invalid or expired token\"}"
                );
                response.getWriter().flush();
            }
        } catch (Exception e) {
            log.error("Authentication error for request {}: {}", request.getRequestURI(), e.getMessage(), e);
            if (!response.isCommitted()) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication error");
                response.setContentType("application/json");
                response.getWriter().write(
                        "{\"message\": \"Unauthorized - Authentication error: " + e.getMessage() + "\"}"
                );
                response.getWriter().flush();
            }
        }
    }

    private void setAuthentication(String accessToken) {
        Claims claims = jwtService.getAllClaims(accessToken);
        String userIdStr = claims.get("userId", String.class);
        Pcic pcic = null;
        if (userIdStr != null) {
            try {
                pcic = pcicRepository.findByIdWithRolesAndPermissions(UUID.fromString(userIdStr)).orElse(null);
            } catch (Exception e) {
                log.warn("Failed to fetch user from repository: {}", e.getMessage());
            }
        }
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
        CustomUserDetails customUser;
        if (pcic != null) {
            customUser = new CustomUserDetails(pcic, authorities);
        } else {
            customUser = new CustomUserDetails(claims, authorities);
        }
        Authentication auth = new UsernamePasswordAuthenticationToken(
                customUser,
                null,
                authorities
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
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
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("ACCESS_TOKEN".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    private String extractRefreshToken(HttpServletRequest request) {
        String headerToken = request.getHeader("X-Refresh-Token");
        if (headerToken != null && headerToken.startsWith("Bearer ")) {
            return headerToken.substring(7);
        }

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("REFRESH_TOKEN".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        return null;
    }
}
