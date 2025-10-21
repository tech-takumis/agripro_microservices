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

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class GatewaySecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        // ✅ WebSocket handshake and info endpoints must always be open
                        .pathMatchers("/ws/**", "/ws", "/ws/info", "/ws/info/**").permitAll()
                        // ✅ Allow preflight
                        .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // ✅ Auth routes
                        .pathMatchers(
                                // Farmer
                                "/api/v1/farmer/auth/login",
                                "/api/v1/farmer/auth/registration",
                                // Agriculture
                                "/api/v1/agriculture/auth/login",
                                "/api/v1/agriculture/auth/registration",
                                // Pcic
                                "/api/v1/pcic/auth/login",
                                "/api/v1/pcic/auth/registration",
                                "/ws/**"
                        ).permitAll()
                        .pathMatchers(
                                // Farmer
                                "/api/v1/farmer/auth/me",
                                "/api/v1/farmer/auth/logout",
                                // Agriculture
                                "/api/v1/agriculture/auth/me",
                                "/api/v1/agriculture/auth/logout",
                                // PCIC
                                "/api/v1/pcic/auth/me",
                                "/api/v1/pcic/auth/logout").authenticated()
                        .pathMatchers("/actuator/**").permitAll()
                        .anyExchange().authenticated()
                )
                // ✅ Add our JWT filter after the authentication point
                .addFilterAt(jwtAuthenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.setAllowedOrigins(List.of(
                "http://localhost:5173",
                "http://localhost:5174",
                "http://localhost:3000"
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
