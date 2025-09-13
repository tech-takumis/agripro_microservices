package com.hashjosh.document.config;

import com.hashjosh.jwtshareable.service.JwtService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        String method = request.getMethod();
        String path = request.getRequestURI();


        if(authHeader == null || authHeader.trim().isEmpty()){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write(
                    """
                            "message": "Unauthorized access token null"
                            """
            );

            response.getWriter().flush();
            return;
        }

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
        String role = claims.get("role", String.class);
        String userId = claims.get("userId", String.class);
        UUID uuid = UUID.fromString(userId);
        List<String> permissions = claims.get("permissions", List.class);
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(role));

        if(!permissions.isEmpty()) {
            permissions.forEach(permission -> authorities.add(new SimpleGrantedAuthority(permission)));
        }

        CustomUserDetail userDetail = new CustomUserDetail(
                uuid,
                username,
                authorities
        );

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetail,
                        null,
                        authorities
                );

        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }
}
