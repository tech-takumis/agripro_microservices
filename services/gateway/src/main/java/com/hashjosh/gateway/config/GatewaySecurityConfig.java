package com.hashjosh.gateway.config;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.GatewayFilterSpec;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class GatewaySecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final TrustedConfig trustedConfig;
    private static final String INTERNAL_SERVICE_HEADER = "X-Internal-Service";
    public static final Set<String> PERMITTED_PATHS = Set.of(
            "/ws/**", "/ws", "/ws/info", "/ws/info/**",
            "/api/v1/farmer/auth/login",
            "/api/v1/farmer/auth/registration",
            "/api/v1/agriculture/auth/login",
            "/api/v1/agriculture/auth/registration",
            "/api/v1/pcic/auth/login",
            "/api/v1/pcic/auth/registration",
            "/actuator/**"
    );

    public static final Set<String> AUTHENTICATED_PATHS = Set.of(
            "/api/v1/farmer/auth/me",
            "/api/v1/farmer/auth/logout",
            "/api/v1/agriculture/auth/me",
            "/api/v1/agriculture/auth/logout",
            "/api/v1/pcic/auth/me",
            "/api/v1/pcic/auth/logout"
    );

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .pathMatchers(PERMITTED_PATHS.toArray(new String[0])).permitAll()
                        .pathMatchers(AUTHENTICATED_PATHS.toArray(new String[0])).authenticated()
                        .anyExchange().authenticated()
                )
                .addFilterAt(jwtAuthenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }

    /**
     * Custom filter logic to skip JWT filter for permitted paths and trusted internal services.
     */
    @Bean
    public WebFilter shouldNotFilterWebFilter() {
        return (exchange, chain) -> {
            String path = exchange.getRequest().getPath().value();
            String internalServiceHeader = exchange.getRequest().getHeaders().getFirst(INTERNAL_SERVICE_HEADER);
            // Check if path is permitted
            boolean isPermitted = PERMITTED_PATHS.stream().anyMatch(path::startsWith);
            // Check if request is from a trusted internal service
            boolean isTrusted = internalServiceHeader != null &&
                    trustedConfig.getInternalServiceIds().contains(internalServiceHeader);
            if (isPermitted || isTrusted) {
                return chain.filter(exchange);
            }
            return jwtAuthenticationFilter.filter(exchange, chain);
        };
    }

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.setAllowedOrigins(List.of(
                "http://localhost:5173",
                "http://localhost:5174",
                "http://localhost:3000" // Add this line
        ));
        corsConfig.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        corsConfig.setAllowedHeaders(List.of("*"));
        corsConfig.setExposedHeaders(List.of("Authorization", "Content-Type"));
        corsConfig.setAllowCredentials(true);
        corsConfig.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);

        return new CorsWebFilter(source);
    }

    @Bean
    public RouteLocator websocketRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("ws_route", r -> r
                        .path("/ws/**", "/ws", "/ws/info", "/ws/info/**")
                        .filters(GatewayFilterSpec::preserveHostHeader)
                        .uri("lb://realtime-service"))
                .build();
    }

}
