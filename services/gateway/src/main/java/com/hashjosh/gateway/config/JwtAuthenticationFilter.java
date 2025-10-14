package com.hashjosh.gateway.config;

import com.hashjosh.jwtshareable.service.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@Slf4j
public class JwtAuthenticationFilter implements WebFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        String accessToken = extractAccessToken(request);
        String refreshToken = extractRefreshToken(request);
        log.info("Received token: {} in request {}", accessToken, request.getURI());

        if (accessToken != null && jwtService.validateToken(accessToken)) {
            String username = jwtService.getUsernameFromToken(accessToken);

            // Authentication object
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(username, null, null);

            // Mutate request headers for downstream services
            ServerHttpRequest mutatedRequest = request.mutate()
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .headers(headers -> {
                        if (refreshToken != null) {
                            headers.set("X-Refresh-Token", refreshToken);
                        }
                    })
                    .build();

            return chain.filter(exchange.mutate().request(mutatedRequest).build())
                    .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));
        }

        // No valid token → proceed without authentication
        return chain.filter(exchange);
    }

    /**
     * Extract access token from header or cookie.
     */
    private String extractAccessToken(ServerHttpRequest request) {
        // 1️⃣ Header
        String bearerToken = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            log.info("Extracted token from Authorization header {}:",bearerToken.substring(7));
            return bearerToken.substring(7);
        }

        // 2️⃣ Cookie
        List<HttpCookie> cookies = request.getCookies().get("ACCESS_TOKEN");
        if (cookies != null && !cookies.isEmpty()) {
            String token =  cookies.getFirst().getValue();
            log.info("Extracted token from ACCESS_TOKEN cookie: {}", token);
            return token;
        }else {
            log.warn("No ACCESS_TOKEN cookie found in request");
        }

        log.warn("No valid token found in headers or cookies");
        return null;
    }

    /**
     * Extract refresh token from header or cookie.
     */
    private String extractRefreshToken(ServerHttpRequest request) {
        String headerToken = request.getHeaders().getFirst("X-Refresh-Token");
        if (headerToken != null && !headerToken.isEmpty()) {
            return headerToken;
        }

        List<HttpCookie> cookies = request.getCookies().get("REFRESH_TOKEN");
        if (cookies != null && !cookies.isEmpty()) {
            return cookies.getFirst().getValue();
        }

        return null;
    }
}