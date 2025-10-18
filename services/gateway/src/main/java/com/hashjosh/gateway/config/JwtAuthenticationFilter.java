package com.hashjosh.gateway.config;

import com.hashjosh.jwtshareable.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter implements WebFilter {

    private final JwtService jwtService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest req = exchange.getRequest();
        String path = req.getURI().getPath();

        // Allow all websocket handshake/info paths through (gateway will forward auth header if present)
        if (path.startsWith("/ws")) {
            // forward existing Authorization header if present and do not block
            return chain.filter(exchange);
        }

        // For regular HTTP API requests: validate token and enrich headers for downstream
        String bearer = req.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (bearer != null && bearer.startsWith("Bearer ")) {
            String token = bearer.substring(7);
            try {
                if (!jwtService.validateToken(token)) {
                    log.warn("Invalid token");
                    exchange.getResponse().setStatusCode(org.springframework.http.HttpStatus.UNAUTHORIZED);
                    return exchange.getResponse().setComplete();
                }

                // Optionally: add internal headers, but preserve Authorization for downstream
                ServerHttpRequest mutated = req.mutate()
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .build();
                return chain.filter(exchange.mutate().request(mutated).build());
            } catch (Exception ex) {
                log.error("Token validation error", ex);
                exchange.getResponse().setStatusCode(org.springframework.http.HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
        }

        // no auth — continue (security chain will enforce)
        return chain.filter(exchange);
    }
}
