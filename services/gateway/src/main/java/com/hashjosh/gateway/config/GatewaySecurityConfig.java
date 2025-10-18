package com.hashjosh.gateway.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class GatewaySecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable) // disable popup
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        // SockJS & raw WS handshake endpoints — permit all (gateway will forward)
                        .pathMatchers("/ws", "/ws/**", "/ws/info", "/ws/info/**").permitAll()
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
                        .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .anyExchange().authenticated()
                );

        // NOTE: do not add custom auth filter here for websocket paths; we let the Gateway JwtFilter forward the header
        return http.build();
    }

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        // only allow frontends you want
        corsConfig.setAllowedOrigins(List.of("http://localhost:5174", "http://localhost:5173", "http://localhost:3000"));
        corsConfig.setAllowedMethods(Arrays.asList("GET","POST","PUT","DELETE","OPTIONS","PATCH"));
        corsConfig.setAllowedHeaders(List.of("*"));
        corsConfig.setAllowCredentials(true);
        corsConfig.setExposedHeaders(List.of(HttpHeaders.AUTHORIZATION, HttpHeaders.CONTENT_TYPE));
        corsConfig.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // register for everything so /ws/info is covered
        source.registerCorsConfiguration("/**", corsConfig);
        return new CorsWebFilter(source);
    }

}
