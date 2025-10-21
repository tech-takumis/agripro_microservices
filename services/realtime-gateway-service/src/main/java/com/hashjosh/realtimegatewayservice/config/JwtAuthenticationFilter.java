package com.hashjosh.realtimegatewayservice.config;

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
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final TrustedService trustedService;
    private static final String INTERNAL_SERVICE_HEADER = "X-Internal-Service";
    private static final String USERID_HEADER = "X-User-Id";


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String uri = request.getRequestURI();
        log.debug("🔍 Processing request for URI: {}", uri);


        // Check for internal service header from gateway
        String internalServiceHeader = request.getHeader(INTERNAL_SERVICE_HEADER);
        if (internalServiceHeader != null) {
            log.debug("🔑 Internal service request from: {}", internalServiceHeader);

            // Check if this is a trusted internal service
            if (trustedService.getInternalServiceIds().contains(internalServiceHeader)) {
                log.debug("✅ Trusted internal service authenticated: {}", internalServiceHeader);
                authenticateInternalService(internalServiceHeader, request);
                filterChain.doFilter(request, response);
                return;
            } else {
                log.warn("⚠️ Untrusted internal service attempted access: {}", internalServiceHeader);
            }
        }

        // Regular token authentication for API requests
        String token = extractToken(request);
        if (token == null) {
            log.debug("❌ No authentication token found");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing or invalid Authorization header");
            return;
        }

        try {
            authenticateRequest(token, request);
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            log.error("🚫 Authentication error: {}", e.getMessage());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication failed: " + e.getMessage());
        } finally {
            SecurityContextHolder.clearContext();
        }

    }

    @Override
    public boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();

        return path.startsWith("/ws") || path.contains("/ws/");
    }

    private void authenticateInternalService(String serviceId, HttpServletRequest request) {
        log.debug("🔐 Authenticating internal service: {}", serviceId);
        String userIdHeader = request.getHeader(USERID_HEADER);
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        new CustomUserDetails(
                                null,
                                userIdHeader,
                                serviceId,
                                null, null, null, null,
                                Set.of(new SimpleGrantedAuthority("ROLE_INTERNAL_SERVICE"))
                        ),
                        null,
                        Set.of(new SimpleGrantedAuthority("ROLE_INTERNAL_SERVICE"))
                );

        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private void authenticateRequest(String token, HttpServletRequest request) {
        if (!jwtService.validateToken(token)) {
            throw new SecurityException("Invalid or expired token");
        }

        Claims claims = jwtService.getAllClaims(token);
        String username = jwtService.getUsernameFromToken(token);
        String userId = claims.get("userId", String.class);
        String firstname = claims.get("firstname", String.class);
        String lastname = claims.get("lastname", String.class);
        String email = claims.get("email", String.class);
        String phone = claims.get("phoneNumber", String.class);
        List<String> claimRoles = claims.get("roles", List.class);
        List<String> claimPermission = claims.get("permissions", List.class);

        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        if (claimRoles != null) {
            claimRoles.forEach(role -> authorities.add(new SimpleGrantedAuthority("ROLE_" + role)));
        }
        if (claimPermission != null) {
            claimPermission.forEach(permission -> authorities.add(new SimpleGrantedAuthority(permission)));
        }

        CustomUserDetails userDetails = new CustomUserDetails(
                token, userId, username, firstname, lastname, email, phone, authorities
        );

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
