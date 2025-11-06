package com.hashjosh.farmer.config;

import com.hashjosh.farmer.service.TokenRenewalService;
import com.hashjosh.jwtshareable.service.JwtService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
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

    private static final String INTERNAL_SERVICE_HEADER = "X-Internal-Service";
    private static final String USER_ID_HEADER = "X-User-Id";
    private static final Set<String> PUBLIC_ENDPOINTS = Set.of(
            "/api/v1/farmer/auth/login",
            "/api/v1/farmer/auth/registration"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                  HttpServletResponse response,
                                  FilterChain filterChain)
            throws ServletException, IOException {

        String requestUri = request.getRequestURI();

        // Skip authentication for public endpoints
        if (PUBLIC_ENDPOINTS.contains(requestUri)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Check for internal service header
        String internalServiceHeader = request.getHeader(INTERNAL_SERVICE_HEADER);
        String userIdHeader = request.getHeader(USER_ID_HEADER);
        UUID userUUIdHeader = null;
        if (userIdHeader != null && !userIdHeader.isEmpty()) {
            try {
                userUUIdHeader = UUID.fromString(userIdHeader);
            } catch (IllegalArgumentException e) {
                log.warn("Invalid X-User-Id header value: {}", userIdHeader);
                if (!response.isCommitted()) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid X-User-Id header value");
                    response.setContentType("application/json");
                    response.getWriter().write(
                        "{\"message\": \"Bad Request - Invalid X-User-Id header value\"}"
                    );
                    response.getWriter().flush();
                }
                return;
            }
        }
        log.debug("X-Internal-Service header: {}, Trusted service IDs: {}", internalServiceHeader, trustedConfig.getInternalServiceIds());

        if (internalServiceHeader != null) {
            if (trustedConfig.getInternalServiceIds().contains(internalServiceHeader)) {
                log.info("Internal service request from {} to {}", internalServiceHeader, requestUri);
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                new CustomUserDetails(internalServiceHeader,userUUIdHeader, Set.of(new SimpleGrantedAuthority("ROLE_INTERNAL_SERVICE"))),
                                null,
                                Set.of(new SimpleGrantedAuthority("ROLE_INTERNAL_SERVICE"))
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
                return;
            } else {
                log.warn("Invalid X-Internal-Service header: {}. Expected one of: {}", internalServiceHeader, trustedConfig.getInternalServiceIds());
                if (!response.isCommitted()) {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid X-Internal-Service header");
                    response.setContentType("application/json");
                    response.getWriter().write(
                            "{\"message\": \"Unauthorized - Invalid X-Internal-Service header\"}"
                    );
                    response.getWriter().flush();
                }
                return;
            }
        }
        String accessToken = extractAccessToken(request);
        String refreshToken = extractRefreshToken(request);
        String clientIp = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");

        // Log extracted tokens and URI
        log.info("[JWT Filter] URI: {} | Extracted access token: {} | Extracted refresh token: {}", requestUri, accessToken, refreshToken);

        // Reject requests without access token or internal service header
        if (accessToken == null) {
            log.warn("Unauthorized request to {}: Missing both X-Internal-Service and Authorization headers", requestUri);
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

        try {
            if (!jwtService.isExpired(accessToken) && jwtService.validateToken(accessToken)) {
                log.info("[JWT Filter] Valid access token for URI: {}", requestUri);
                setAuthentication(accessToken, request);
                filterChain.doFilter(request, response);
                return;
            }

            // Handle expired access token with valid refresh token
            if (refreshToken != null) {
                log.info("[JWT Filter] Expired access token, valid refresh token for URI: {}", requestUri);
                Claims claim = jwtService.getClaimsAllowExpired(accessToken);
                String username = claim.getSubject();
                String userId = claim.get("userId", String.class);

                // Build new claims for token renewal
                Map<String, Object> claimsMap = new HashMap<>();
                claimsMap.put("userId", userId);

                Map<String, String> newTokens = tokenRenewalService.refreshTokens(
                        UUID.fromString(userId), refreshToken, username,
                        claimsMap, clientIp, userAgent, false);

                // Set authentication with new access token
                setAuthentication(newTokens.get("accessToken"), request);

                // Add new tokens as cookies
                addTokenCookies(response, newTokens.get("accessToken"), newTokens.get("refreshToken"));
                filterChain.doFilter(request, response);
                return;
            }

            log.warn("[JWT Filter] Invalid or expired token for URI: {}", requestUri);
            if (!response.isCommitted()) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired token");
                response.setContentType("application/json");
                response.getWriter().write(
                        "{\"message\": \"Unauthorized - Invalid or expired token\"}"
                );
                response.getWriter().flush();
            }
        }catch (Exception e) {
            log.error("[JWT Filter] Authentication error for URI: {} | Exception: {}", requestUri, e.getMessage(), e);
            if (!response.isCommitted()) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error");
                response.setContentType("application/json");
                response.getWriter().write(
                        "{\"message\": \"Unauthorized - Authentication error: " + e.getMessage() + "\"}"
                );
                response.getWriter().flush();
            }
        }
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
        return null;
    }

    private String extractRefreshToken(HttpServletRequest request) {
        String headerToken = request.getHeader("X-Refresh-Token");
        return (headerToken != null && !headerToken.isEmpty()) ? headerToken : null;
    }
}