package com.hashjosh.workflow.config;

import com.hashjosh.jwtshareable.service.JwtService;
import com.hashjosh.workflow.clients.UserResponse;
import com.hashjosh.workflow.clients.UserServiceClient;
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
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter  extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserServiceClient userServiceClient;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        
        // Skip authentication for public endpoints
        if (isPublicEndpoint(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Check if authorization header exists and is valid
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            sendErrorResponse(response, "Missing or invalid Authorization header");
            return;
        }

        String token = authHeader.substring(7).trim();
        if (token.isEmpty()) {
            sendErrorResponse(response, "Empty token");
            return;
        }

        // Validate token
        if (!jwtService.validateToken(token)) {
            sendErrorResponse(response, "Invalid or expired token");
            return;
        }

        Claims claims = jwtService.getAllClaims(token);
        String username = jwtService.getUsernameFromToken(token);
        String userId = claims.get("userId", String.class);
        String tenantId = claims.get("tenantId", String.class);

        UserResponse user = userServiceClient.getUserById(UUID.fromString(userId), token);

        Set<SimpleGrantedAuthority> roles = new HashSet<>();

        user.getRoles().forEach(role -> {
            roles.add(new SimpleGrantedAuthority("ROLE_"+role.getName()));
            role.getPermissions().forEach(
                    permission -> roles.add(new SimpleGrantedAuthority(permission.getName()))
            );

        });

        CustomUserDetails userDetails = new CustomUserDetails(
                token,
                userId,
                tenantId,
                username,
                user.getEmail(),
                roles
        );

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        roles
                );

        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        // Set security context
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        try {
            filterChain.doFilter(request, response);
        } finally {
            // Clear the context after the request is processed
            SecurityContextHolder.clearContext();
        }
    }

    private void sendErrorResponse(HttpServletResponse response, String message) throws IOException {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write(
                """
                {
                  "message": "%s"
                }
                """.formatted(message)
        );
        response.getWriter().flush();
    }

    private boolean isPublicEndpoint(HttpServletRequest request) {
        // Implement logic to check if the endpoint is public
        return false;
    }
}