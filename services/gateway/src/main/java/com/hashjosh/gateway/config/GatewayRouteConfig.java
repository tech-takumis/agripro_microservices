package com.hashjosh.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.GatewayFilterSpec;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayRouteConfig {

//    @Bean
//    public RouteLocator websocketRoutes(RouteLocatorBuilder builder) {
//        return builder.routes()
//                // WebSocket / SockJS route(s) — include /ws/info explicitly
//                .route("communication_ws", r -> r
//                        .path("/ws", "/ws/**", "/ws/info", "/ws/info/**")
//                        .filters(GatewayFilterSpec::preserveHostHeader) // keep host, don't remove Authorization
//                        .uri("lb://communication-service"))
//                // keep other routes as you had them...
//                .build();
//    }
}