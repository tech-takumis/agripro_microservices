package com.hashjosh.document.config;

import com.hashjosh.document.clients.dto.UserResponse;
import com.hashjosh.document.clients.UserServiceClient;
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
    private final UserServiceClient userServiceClient;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        String token = authHeader.substring(7);

        if(token.trim().isEmpty()){
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write(
                    """
                            "message": "Unauthorized - Invalid or null token"
                            """
            );
            response.getWriter().flush();
            return;
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
            return; // stop filter chain
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

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        try{
            filterChain.doFilter(request, response);
        }finally {
            SecurityContextHolder.clearContext();
        }
    }
}
