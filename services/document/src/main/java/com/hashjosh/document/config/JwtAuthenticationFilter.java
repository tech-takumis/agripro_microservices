package com.hashjosh.document.config;

import com.hashjosh.jwtshareable.service.JwtService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.*;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final TrustedConfig trustedConfig;
    private static final String INTERNAL_SERVICE_HEADER = "X-Internal-Service";
    private static final String USERID_HEADER = "X-User-Id";
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String internalServiceHeader = request.getHeader(INTERNAL_SERVICE_HEADER);
        String userIdHeader = request.getHeader(USERID_HEADER);
        if (internalServiceHeader != null && trustedConfig.getInternalServiceIds().contains(internalServiceHeader)) {
            // Allow internal service access without JWT
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            new CustomUserDetails(
                                    null, // No token
                                    userIdHeader,// Unique userId
                                    internalServiceHeader, // Use service ID as username
                                    null, null, null, null, // No firstname, lastname, email, phone
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


        // JWT validation for non-internal requests
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing or invalid Authorization header");
            response.setContentType("application/json");
            response.getWriter().write(
                    "{\"message\": \"Unauthorized - Missing or invalid Authorization header\"}"
            );
            response.getWriter().flush();
            return;
        }

        String token = authHeader.substring(7);
        if (token.trim().isEmpty()) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write(
                    "{\"message\": \"Unauthorized - Invalid or null token\"}"
            );
            response.getWriter().flush();
            return;
        }



        // NOTE: I THINK THIS IS UNNECESSARY SINCE MY GATEWAY ALREADY IMPLEMENTS
        // TOKEN RENEWAL IF THE TOKEN EXPIRED
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



        if (!jwtService.validateToken(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write(
                    """
                    {
                      "message": "Unauthorized: invalid or expired token"
                    }
                    """
            );
            response.getWriter().flush();
        }
    }
}
