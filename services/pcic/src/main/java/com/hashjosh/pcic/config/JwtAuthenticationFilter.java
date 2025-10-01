package com.hashjosh.pcic.config;


import com.hashjosh.jwtshareable.service.JwtService;
import com.hashjosh.pcic.entity.Pcic;
import com.hashjosh.pcic.service.TokenRenewalService;
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
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService customUserDetailsService;
    private final TokenRenewalService tokenRenewalService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        try {
            String accessToken = extractAccessToken(request);
            String refreshToken = extractRefreshToken(request);
            String clientIp = request.getRemoteAddr();
            String path = request.getRequestURI();
            String userAgent = request.getHeader("User-Agent");

            log.info("Refresh and access token received from {} the user " +
                    "jwt authentication filter access token: {} refresh token{}"
                    , path, accessToken, refreshToken);

            if (accessToken != null) {
                if (!jwtService.isExpired(accessToken) && jwtService.validateToken(accessToken)) {
                    // ✅ Normal authentication flow
                    setAuthentication(accessToken);
                } else if (refreshToken != null &&
                        jwtService.validateRefreshToken(refreshToken, clientIp, userAgent)) {
                    // 🔄 Access expired, refresh valid → generate new tokens
                    Claims claims = jwtService.getClaimsAllowExpired(accessToken);
                    String username = claims.getSubject();
                    String userId = claims.get("userId", String.class);

                    // Build the new claims for new token
                    Map<String, Object> claimsMap = new HashMap<>();
                    claimsMap.put("userId", userId);

                    Map<String, String> newTokens = tokenRenewalService.refreshTokens(
                            UUID.fromString(userId), refreshToken, username,
                            null, claimsMap, clientIp, userAgent, false);

                    // ⬆ Set Authentication for downstream
                    setAuthentication(newTokens.get("accessToken"));

                    // 🍪 Return as cookies
                    addTokenCookies(response, newTokens.get("accessToken"), newTokens.get("refreshToken"));
                }
            }

            filterChain.doFilter(request, response);

        } finally {
            // No cleanup needed
        }
    }

    private void setAuthentication(String accessToken) {
        Claims claims = jwtService.getAllClaims(accessToken);
        String username = claims.getSubject();

        CustomUserDetails customUser =
                (CustomUserDetails) customUserDetailsService.loadUserByUsername(username);

        Pcic pcic = customUser.getPcic();
        List<SimpleGrantedAuthority> roles = new ArrayList<>();

        pcic.getRoles().forEach(role -> {
            roles.add(new SimpleGrantedAuthority("ROLE_" + role.getSlug().toUpperCase()));
            role.getPermissions().forEach(permission -> {
                roles.add(new SimpleGrantedAuthority(permission.getSlug().toUpperCase()));
            });
        });

        Authentication auth = new UsernamePasswordAuthenticationToken(customUser,
                null, roles);
        SecurityContextHolder.getContext().setAuthentication(auth);
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