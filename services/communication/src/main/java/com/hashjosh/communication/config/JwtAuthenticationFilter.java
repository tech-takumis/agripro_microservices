package com.hashjosh.communication.config;

import com.hashjosh.communication.properties.TrustedProperties;
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
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService   jwtService;
    private final TrustedProperties trustedProperties;
    private static final String INTERNAL_SERVICE_HEADER = "X-Internal-Service";
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String uri = request.getRequestURI();
        log.info("Processing request for URI: {}", uri);

        // Skip authentication for WebSocket endpoints
        if (uri.startsWith("/ws")) {
            filterChain.doFilter(request, response);
            return;
        }

        String internalServiceHeader = request.getHeader(INTERNAL_SERVICE_HEADER);
        if(internalServiceHeader != null && trustedProperties.getInternalServiceIds().contains(internalServiceHeader) ){
            // Allow internal service access without JWT
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            new CustomUserDetails(
                                    null,
                                    "internal-service"+internalServiceHeader,
                                    internalServiceHeader,
                                    null,null,null,null, // No firstname,lastname,email,and phone
                                    Set.of(new SimpleGrantedAuthority("ROLE_INTERNAL_SERVICE"))
                            ),
                            null,
                            Set.of(new SimpleGrantedAuthority("ROLE_INTERNAL_SERVICE"))
                    );

            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);
            return;
        }

        String token = extractAccessToken(request);

        if(token == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing or invalid Authorization header");
            response.setContentType("application/json");
            response.getWriter().write(
                    "{\"message\": \"Unauthorized - Missing or invalid Authorization header\"}"
            );
            response.getWriter().flush();
            return;
        }

        if (!jwtService.validateToken(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write(
                    "{\"message\": \"Unauthorized: invalid or expired token\"}"
            );
            response.getWriter().flush();
            return;
        }

        Claims claims = jwtService.getAllClaims(token);
        String username = jwtService.getUsernameFromToken(token);
        String userId = claims.get("userId", String.class);
        String firstname = claims.get("firstname", String.class);
        String lastname = claims.get("lastname", String.class);
        String email = claims.get("email", String.class);
        String phone = claims.get("phone", String.class);
        List<String> claimRoles = claims.get("roles", List.class);
        List<String> claimPermission = claims.get("permissions", List.class);

        Set<SimpleGrantedAuthority> roles = new HashSet<>();
        if (claimRoles != null) {
            for (String role : claimRoles) {
                roles.add(new SimpleGrantedAuthority("ROLE_" + role));
            }
        }
        if (claimPermission != null) {
            for (String permission : claimPermission) {
                roles.add(new SimpleGrantedAuthority(permission));
            }
        }

        CustomUserDetails userDetails = new CustomUserDetails(
                token, userId, username, firstname, lastname, email, phone, roles
        );

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails, null, roles
                );

        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        try {
            filterChain.doFilter(request, response);
        } finally {
            SecurityContextHolder.clearContext();
        }

    }

    private String extractAccessToken(HttpServletRequest request) {
        // First try Authorization header
        String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            log.info("Extracted token from Authorization header for path: {}", request.getRequestURI());
            return bearerToken.substring(7);
        }

        // Then try cookies
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("ACCESS_TOKEN".equals(cookie.getName())) {
                    log.info("Extracted token from ACCESS_TOKEN cookie for path: {}", request.getRequestURI());
                    return cookie.getValue();
                }
            }
        }

        // Finally try custom header
        String customToken = request.getHeader("X-Auth-Token");
        if (customToken != null) {
            log.info("Extracted token from X-Auth-Token header for path: {}", request.getRequestURI());
            return customToken;
        }

        log.warn("No token found in headers or cookies for path: {}", request.getRequestURI());
        return null;
    }
}
