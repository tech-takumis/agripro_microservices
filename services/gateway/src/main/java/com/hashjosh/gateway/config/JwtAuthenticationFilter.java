package com.hashjosh.gateway.config;

import com.hashjosh.gateway.service.TenantContext;
import com.hashjosh.jwtshareable.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

import java.util.List;

@RequiredArgsConstructor
public class JwtAuthenticationFilter implements WebFilter {

    private final JwtService jwtService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        String accessToken = extractAccessToken(request);
        String refreshToken = extractRefreshToken(request);

        if (accessToken != null && jwtService.validateToken(accessToken)) {
            // Extract username and tenant
            String username = jwtService.getUsernameFromToken(accessToken);
            String tenantId = jwtService.getAllClaims(accessToken).get("tenantId", String.class);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(username, null, null);

            // ✅ Mutate request to add downstream headers
            ServerHttpRequest mutatedRequest = request.mutate()
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .headers(headers -> {
                        if (refreshToken != null) {
                            headers.set("X-Refresh-Token", refreshToken);
                        }
                    })
                    .build();

            return chain.filter(exchange.mutate().request(mutatedRequest).build())
                    .contextWrite(context -> {
                        Context securityContext = ReactiveSecurityContextHolder.withAuthentication(authentication);
                        if (tenantId != null) {
                            securityContext = TenantContext.setTenant(securityContext, tenantId);
                        }
                        return securityContext;
                    });
        }

        return chain.filter(exchange);
    }

    /**
     * Extracts Access Token from Authorization header or cookies.
     */
    private String extractAccessToken(ServerHttpRequest request) {
        String bearerToken = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        List<HttpCookie> cookies = request.getCookies().get("access_token");
        if (cookies != null && !cookies.isEmpty()) {
            return cookies.get(0).getValue();
        }
        return null;
    }

    /**
     * Extracts Refresh Token from header or cookies.
     * Priority: Header `X-Refresh-Token` → Cookie `refresh_token`
     */
    private String extractRefreshToken(ServerHttpRequest request) {
        String headerToken = request.getHeaders().getFirst("X-Refresh-Token");
        if (headerToken != null && !headerToken.isEmpty()) {
            return headerToken;
        }
        List<HttpCookie> cookies = request.getCookies().get("refresh_token");
        if (cookies != null && !cookies.isEmpty()) {
            return cookies.get(0).getValue();
        }
        return null;
    }
}
