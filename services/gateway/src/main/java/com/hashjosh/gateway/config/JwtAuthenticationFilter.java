package com.hashjosh.gateway.config;

import com.hashjosh.jwtshareable.service.JwtService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.*;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter implements WebFilter {

    private final JwtService jwtService;
    private final TrustedConfig trustedConfig;

    private static final List<String> PUBLIC_PATHS = List.of(
            "/ws", "/ws/", "/ws/info", "/ws/info/",
            "/api/v1/users/auth/login","/api/v1/users/auth/registration"
    );

    private static final String INTERNALSERVICE_HEADER = "X-Internal-Service";
    private static final String USERID_HEADER = "X-User-Id";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        // Use maintainable shouldNotFilter logic
        BypassDecision bypass = shouldNotFilter(request);
        if (bypass.isBypass()) {
            log.debug(bypass.getLogMessage(), request.getURI().getPath());
            if (bypass.getAuthentication() != null) {
                // Internal service trust
                return chain.filter(exchange)
                        .contextWrite(ReactiveSecurityContextHolder.withAuthentication(bypass.getAuthentication()));
            } else {
                return chain.filter(exchange);
            }
        }

        // 🧩 Extract token
        String token = extractAccessToken(request);
        String path = request.getURI().getPath();
        if (token == null) {
            log.warn("❌ No access token found for request: {}", path);
            return this.unauthorized(exchange, "Missing token");
        }

        try {
            if (!jwtService.validateToken(token)) {
                log.warn("🚫 Invalid or expired JWT for path: {}", path);
                return this.unauthorized(exchange, "Invalid or expired token");
            }

            String username = jwtService.getUsernameFromToken(token);
            Claims claims = jwtService.getAllClaims(token);
            List<SimpleGrantedAuthority> authorities = extractAuthorities(claims);

            Authentication authentication = new UsernamePasswordAuthenticationToken(username, null, authorities);

            log.debug("✅ Authenticated user '{}' with {} authorities", username, authorities.size());

            // Mutate headers for downstream
            ServerHttpRequest mutated = request.mutate()
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .header("X-User-Username", username)
                    .build();

            return chain.filter(exchange.mutate().request(mutated).build())
                    .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));

        } catch (Exception e) {
            log.error("💥 JWT auth failed: {}", e.getMessage());
            return this.unauthorized(exchange, "Authentication failed: " + e.getMessage());
        }
    }

    /**
     * Result class for shouldNotFilter decision.
     */
    private static class BypassDecision {
        private final boolean bypass;
        private final String logMessage;
        private final Authentication authentication;

        public BypassDecision(boolean bypass, String logMessage, Authentication authentication) {
            this.bypass = bypass;
            this.logMessage = logMessage;
            this.authentication = authentication;
        }

        public boolean isBypass() { return bypass; }
        public String getLogMessage() { return logMessage; }
        public Authentication getAuthentication() { return authentication; }

        public static BypassDecision skip(String logMsg) {
            return new BypassDecision(true, logMsg, null);
        }
        public static BypassDecision skipWithAuth(String logMsg, Authentication authentication) {
            return new BypassDecision(true, logMsg, authentication);
        }
        public static BypassDecision never() {
            return new BypassDecision(false, null, null);
        }
    }

    /**
     * Determines if filtering for the request should be bypassed, and for what reason.
     */
    private BypassDecision shouldNotFilter(ServerHttpRequest request) {
        String path = request.getURI().getPath();

        // Skip WebSocket and OPTIONS
        if (path.startsWith("/ws") || "OPTIONS".equalsIgnoreCase(request.getMethod().name())) {
            return BypassDecision.skip("🔓 Skipping JWT auth for WebSocket or OPTIONS request: {}");
        }

        // Trusted internal service header bypass
        String internalServiceHeader = request.getHeaders().getFirst(INTERNALSERVICE_HEADER);
        String userIdHeader = request.getHeaders().getFirst(USERID_HEADER);
        if (internalServiceHeader != null && trustedConfig.getInternalServiceIds().contains(internalServiceHeader)) {
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                userIdHeader,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_INTERNAL_SERVICE"))
            );
            return BypassDecision.skipWithAuth("🔐 Trusted internal service access granted: {}", authentication);
        }

        // Public auth endpoints bypass
        if (PUBLIC_PATHS.stream().anyMatch(path::startsWith)) {
            return BypassDecision.skip("🟢 Public route, skipping authentication: {}");
        }

        return BypassDecision.never();
    }

    private List<SimpleGrantedAuthority> extractAuthorities(Claims claims) {
        List<String> roles = claims.get("roles", List.class);
        List<String> permissions = claims.get("permissions", List.class);
        Set<String> combined = new HashSet<>();
        if (roles != null) roles.forEach(r -> combined.add("ROLE_" + r));
        if (permissions != null) combined.addAll(permissions);
        return combined.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    private String extractAccessToken(ServerHttpRequest request) {
        // 1️⃣ Cookie
        List<HttpCookie> cookies = request.getCookies().get("ACCESS_TOKEN");
        if (cookies != null && !cookies.isEmpty()) {
            return cookies.get(0).getValue();
        }

        // 2️⃣ Header
        String header = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }

        return null;
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        log.warn("Unauthorized: {} -> {}", exchange.getRequest().getURI().getPath(), message);
        exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
        return exchange.getResponse().setComplete();
    }
}
