package com.hashjosh.userservicev2.config;

import com.hashjosh.userservicev2.models.Role;
import com.hashjosh.userservicev2.models.User;
import com.hashjosh.userservicev2.repository.RoleRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private final JwtUtil jwtUtil;
    private final RoleRepository roleRepository;


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String jwt = extractJwt(request);

        if (jwt != null && jwtUtil.validateToken(jwt)) {
            String username = jwtUtil.getUsernameFromToken(jwt);
            Claims claims = jwtUtil.getAllClaimsFromToken(jwt);

            String role  = claims.get("role", String.class);
            Long userId = claims.get("userId", Long.class);
            List<String> permissions = claims.get("permissions", List.class);

            List<GrantedAuthority> authorities = new ArrayList<>();

            System.out.println("Claim roles in JwtAuthenticationFilter class:::: " + role);
            System.out.println("Claim permissions in JwtAuthenticationFilter class:::: " + permissions);


            authorities.add(new SimpleGrantedAuthority("ROLE_"+role));
            if(permissions != null && !permissions.isEmpty()) {
                permissions.forEach(p -> authorities.add(new SimpleGrantedAuthority(p)));
            }

            Optional<Role> roleOpt = roleRepository.findByName(role);

            if(roleOpt.isPresent()) {
              Role roleObj = roleOpt.get();
                CustomUserDetails userDetails = new CustomUserDetails(
                    new User(username,roleObj,userId)
                );

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, authorities);
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }

    private String extractJwt(HttpServletRequest request) {
        // Check cookie (Web)
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("jwt".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        // Check Authorization header (Mobile)
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}
