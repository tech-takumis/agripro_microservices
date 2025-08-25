package com.hashjosh.gatewayservice.config;

import jakarta.ws.rs.core.HttpHeaders;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Profile("gateway_security")
public class JwtAuthenticationFilter implements WebFilter {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final JwtUtil jwtUtil;

    private String extractJwt(ServerHttpRequest request) {
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        // check cookies
        HttpCookie cookie = request.getCookies().getFirst("jwt");
        logger.info("JWT cookie found: " + cookie);
        return cookie != null ? cookie.getValue() : null;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String token = extractJwt(exchange.getRequest());
        String path = exchange.getRequest().getURI().getPath();
        if(path.startsWith("/api/v1/auth/")){
            return chain.filter(exchange); // skip the auth
        }

        if (token == null || !jwtUtil.validateToken(token)) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
        return chain.filter(exchange);
    }
}

