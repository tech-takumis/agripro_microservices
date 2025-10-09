package com.hashjosh.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class GatewaySecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public GatewaySecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // ✅ Enable CORS
//                .cors(ServerHttpSecurity.CorsSpec::disable) // Disable CORS for testing purposes only
                .authorizeExchange(exchanges -> exchanges
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
                .addFilterAt(jwtAuthenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        // Only set each allowed origin once
        config.setAllowedOrigins(List.of("http://localhost:5174", "http://localhost:3000"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // Route websocket upgrade requests via ws://
                .route("websocket_route", r -> r.path("/ws/**")
                        .and().header("Upgrade", "websocket")
                        .uri("ws://communication-service"))
                // Route SockJS info endpoint via http://
                .route("sockjs_info_route", r -> r.path("/ws/info")
                        .uri("lb://communication-service"))
                .build();
    }

}
