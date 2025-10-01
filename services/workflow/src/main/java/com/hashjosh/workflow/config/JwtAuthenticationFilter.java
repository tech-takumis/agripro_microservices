package com.hashjosh.workflow.config;

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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter  extends OncePerRequestFilter {

    private final JwtService jwtService;
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
        String firstname = claims.get("firstname", String.class);
        String lastname = claims.get("lastname", String.class);
        String email = claims.get("email", String.class);
        String phone = claims.get("phone", String.class);
        List<String> claimRoles = claims.get("roles", List.class);
        List<String> claimPermission = claims.get("permissions", List.class);


        Set<SimpleGrantedAuthority> roles = new HashSet<>();

        if(claimRoles != null){
            for(String role : claimRoles){
                roles.add(new SimpleGrantedAuthority("ROLE_"+role));
            }
        }

        if(claimPermission != null){
            for(String permission : claimPermission){
                roles.add(new SimpleGrantedAuthority(permission));
            }
        }


        CustomUserDetails userDetails = new CustomUserDetails(
                token,
                userId,
                username,
                firstname,
                lastname,
                email,
                phone,
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