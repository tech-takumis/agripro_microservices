package com.hashjosh.realtimegatewayservice.config;

import com.hashjosh.jwtshareable.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.broker.BrokerAvailabilityEvent;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.Map;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
@Slf4j
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Lazy
    private final JwtService jwtService;


    @Bean
    public ApplicationListener<BrokerAvailabilityEvent> brokerListener() {
        return event -> log.info("📡 Broker available: {}", event.isBrokerAvailable());
    }

    // ------------------------------------------------------------------------
    // Message Broker Configuration
    // ------------------------------------------------------------------------
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic", "/queue"); // ✅ remove "/user" here
        registry.setApplicationDestinationPrefixes("/app");
        registry.setUserDestinationPrefix("/user"); // ✅ only here
    }

    // ------------------------------------------------------------------------
    // WebSocket Endpoints
    // ------------------------------------------------------------------------
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        String[] allowedOrigins = {
                "http://localhost:5173",
                "http://localhost:5174",
                "http://localhost:9001",
                "http://localhost:9040"
        };

        // Raw WebSocket endpoint for Flutter / native clients
        registry.addEndpoint("/ws")
                .setAllowedOrigins(allowedOrigins)
                .addInterceptors(new WebSocketHandshakeInterceptor(jwtService))
                .setHandshakeHandler(new WebSocketHandshakeHandler());
    }

    // ------------------------------------------------------------------------
    // STOMP Channel Interceptor
    // ------------------------------------------------------------------------
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {

            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
                StompCommand command = accessor.getCommand();

                if (command == null) return message;

                switch (command) {
                    case CONNECT -> handleConnect(accessor);
                    case SUBSCRIBE -> handleSubscribe(accessor);
                    case SEND -> handleSend(accessor);
                    case DISCONNECT -> handleDisconnect(accessor);
                    default -> {}
                }
                return message;
            }

            private void handleConnect(StompHeaderAccessor accessor) {
                // ✅ Don’t overwrite Principal if already set by handshake
                if (accessor.getUser() != null) {
                    log.debug("User already set by handshake: {}", accessor.getUser().getName());
                    return;
                }

                String authHeader = accessor.getFirstNativeHeader("Authorization");
                if (authHeader == null)
                    authHeader = accessor.getFirstNativeHeader("authorization");

                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    String token = authHeader.substring(7);
                    try {
                        if (jwtService.validateToken(token)) {
                            var claims = jwtService.getAllClaims(token);
                            String userId = claims.get("userId", String.class);
                            String username = jwtService.getUsernameFromToken(token);

                            accessor.setUser(new WebSocketPrincipal(userId));
                            log.info("✅ STOMP CONNECT authenticated: {} ({})", username, userId);
                        } else {
                            log.warn("⚠️ Invalid JWT during STOMP CONNECT");
                        }
                    } catch (Exception e) {
                        log.error("🚫 Error validating STOMP token: {}", e.getMessage());
                    }
                } else {
                    log.warn("⚠️ Missing or malformed Authorization header during CONNECT");
                }
            }

            private void handleSubscribe(StompHeaderAccessor accessor) {
                Principal user = accessor.getUser();
                String destination = accessor.getDestination();

                if (user == null || destination == null) {
                    log.warn("⚠️ SUBSCRIBE missing user or destination");
                    return;
                }

                // ✅ Relaxed checks for user or queue destinations
                if (destination.startsWith("/user/") || destination.startsWith("/queue/") || destination.startsWith("/topic/")) {
                    log.info("📡 {} subscribed to {}", user.getName(), destination);
                } else {
                    log.debug("Ignored subscription to {}", destination);
                }
            }

            private void handleSend(StompHeaderAccessor accessor) {
                Principal user = accessor.getUser();
                String destination = accessor.getDestination();

                if (user == null) {
                    log.warn("⚠️ SEND from unauthenticated user");
                    throw new SecurityException("Unauthenticated SEND attempt");
                }

                log.info("✉️ {} sent message to {}", user.getName(), destination);
            }

            private void handleDisconnect(StompHeaderAccessor accessor) {
                Principal user = accessor.getUser();
                if (user != null) {
                    log.info("👋 User disconnected: {}", user.getName());
                }
            }
        });
    }

    // ------------------------------------------------------------------------
    // Custom Handshake Interceptor
    // ------------------------------------------------------------------------
    @Slf4j
    public static class WebSocketHandshakeInterceptor implements HandshakeInterceptor {
        private static final String AUTHORIZATION_HEADER = "Authorization";
        private final JwtService jwtService;

        public WebSocketHandshakeInterceptor(JwtService jwtService) {
            this.jwtService = jwtService;
        }

        @Override
        public boolean beforeHandshake(ServerHttpRequest request,
                                       ServerHttpResponse response,
                                       WebSocketHandler wsHandler,
                                       Map<String, Object> attributes) {
            log.info("⚡ WebSocket handshake: {}", request.getURI());
            try {
                String token = null;

                // ✅ 1. Try header (Flutter, Node, etc.)
                List<String> authHeaders = request.getHeaders().get("Authorization");
                if (authHeaders == null || authHeaders.isEmpty())
                    authHeaders = request.getHeaders().get("authorization");

                if (authHeaders != null && !authHeaders.isEmpty() && authHeaders.get(0).startsWith("Bearer ")) {
                    token = authHeaders.get(0).substring(7);
                    log.debug("Found token in Authorization header");
                }

                // ✅ 2. Try query parameter (Browser)
                if (token == null) {
                    URI uri = request.getURI();
                    String query = uri.getQuery();
                    if (query != null && query.contains("token=")) {
                        token = query.substring(query.indexOf("token=") + 6);
                        log.debug("Found token in ?token= query param");
                    }
                }

                if (token != null && jwtService.validateToken(token)) {
                    var claims = jwtService.getAllClaims(token);
                    String userId = claims.get("userId", String.class);
                    String username = jwtService.getUsernameFromToken(token);

                    attributes.put("authenticated", true);
                    attributes.put("userId", userId);
                    attributes.put("username", username);
                    attributes.put("token", token);
                    log.info("✅ Handshake authenticated user: {} ({})", username, userId);
                } else {
                    attributes.put("authenticated", false);
                    log.warn("⚠️ Invalid or missing JWT during handshake");
                }

            } catch (Exception e) {
                log.error("🚫 Error during handshake: {}", e.getMessage());
                attributes.put("authenticated", false);
            }

            return true;
        }


        @Override
        public void afterHandshake(
                org.springframework.http.server.ServerHttpRequest request,
                org.springframework.http.server.ServerHttpResponse response,
                WebSocketHandler wsHandler,
                Exception exception
        ) {
            if (exception != null) {
                log.error("❌ WebSocket handshake failed: {}", exception.getMessage());
            } else {
                log.info("✅ WebSocket handshake complete");
            }
        }
    }

    // ------------------------------------------------------------------------
    // Handshake Handler (creates Principal)
    // ------------------------------------------------------------------------
    @Slf4j
    public static class WebSocketHandshakeHandler extends DefaultHandshakeHandler {
        @Override
        protected Principal determineUser(
                org.springframework.http.server.ServerHttpRequest request,
                WebSocketHandler wsHandler,
                Map<String, Object> attributes
        ) {
            Boolean authenticated = (Boolean) attributes.get("authenticated");

            if (Boolean.TRUE.equals(authenticated)) {
                String userId = (String) attributes.get("userId");
                String username = (String) attributes.get("username");
                log.info("[WebSocket] Principal set: {} ({})", userId, username);
                return new WebSocketPrincipal(userId);
            }

            String anonymousId = "anonymous-" + System.currentTimeMillis();
            log.info("[WebSocket] Anonymous connection: {}", anonymousId);
            return new WebSocketPrincipal(anonymousId);
        }
    }

    // ------------------------------------------------------------------------
    // Principal Implementation
    // ------------------------------------------------------------------------
    public static class WebSocketPrincipal implements Principal {
        private final String name;

        public WebSocketPrincipal(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

}