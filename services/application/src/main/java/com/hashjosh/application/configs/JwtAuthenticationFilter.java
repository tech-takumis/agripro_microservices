package com.hashjosh.application.configs;

import com.hashjosh.jwtshareable.service.JwtService;
import com.hashjosh.jwtshareable.service.TenantContext;
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
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        try {
            String accessToken = extractAccessToken(request);

            if (accessToken != null && !jwtService.isExpired(accessToken) && jwtService.validateToken(accessToken)) {
                    // ✅ Normal authentication flow
                    setAuthentication(accessToken);
             }

            filterChain.doFilter(request, response);

        } finally {
            TenantContext.clear();
        }
    }

    private void setAuthentication(String accessToken) {
        Claims claims = jwtService.getAllClaims(accessToken);
        String username = claims.getSubject();
        String tenantId = claims.get("tenantId", String.class);
        String userId = claims.get("userId", String.class);

        String role = claims.get("role", String.class);
        List<SimpleGrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_"+role));
        if (tenantId != null) TenantContext.setTenantId(tenantId);

        CustomUserDetails customUserDetails = new CustomUserDetails(
                userId,
                username,
                authorities
        );

        Authentication auth = new UsernamePasswordAuthenticationToken(customUserDetails, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }


    private String extractAccessToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

}