package com.hashjosh.communication.config;

import com.hashjosh.communication.exception.InvalidJwtException;
import com.hashjosh.jwtshareable.service.JwtService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
@Slf4j
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtService  jwtService;
    private static final String TOKEN_HEADER = "X-Auth-Token";

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry){
        registry.enableSimpleBroker("/topic","/queue"); // For receiving messages
        registry.setApplicationDestinationPrefixes("/app"); // For sending messages
        registry.setUserDestinationPrefix("/user"); // For private messages
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry){
        registry.addEndpoint("/ws")
                .addInterceptors(new HandshakeInterceptor() {
                    @Override
                    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
                        // First, try to get token from Authorization header (passed by gateway)
                        String token = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
                        if (token != null && token.startsWith("Bearer ")) {
                            attributes.put(TOKEN_HEADER, token.substring(7));
                            log.info("WebSocket handshake: Found token in Authorization header.");
                            return true;
                        }

                        // If not found, try to get token from cookie
                        if (request instanceof ServletServerHttpRequest servletRequest) {
                            Cookie[] cookies = servletRequest.getServletRequest().getCookies();
                            if (cookies != null) {
                                for (Cookie cookie : cookies) {
                                    if ("ACCESS_TOKEN".equals(cookie.getName())) {
                                        attributes.put(TOKEN_HEADER, cookie.getValue());
                                        log.info("WebSocket handshake: Found token in ACCESS_TOKEN cookie.");
                                        return true;
                                    }
                                }
                            }
                        }
                        log.warn("WebSocket handshake: No token found in headers or cookies.");
                        return true; // Allow connection to proceed, authentication is handled in the interceptor
                    }

                    @Override
                    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
                        // No-op
                    }
                })
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            private final Map<String, Authentication> sessionAuthMap = new ConcurrentHashMap<>();

            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
                String sessionId = accessor.getSessionId();
                StompCommand command = accessor.getCommand();

                log.info("Processing {} command for session {}", command, sessionId);

                if (StompCommand.CONNECT.equals(command)) {
                    Map<String, Object> sessionAttrs = accessor.getSessionAttributes();
                    if (sessionAttrs == null) {
                        log.error("Session attributes are null during CONNECT");
                        throw new InvalidJwtException("No session attributes found");
                    }

                    String token = (String) sessionAttrs.get(TOKEN_HEADER);
                    log.info("CONNECT - Session ID: {}, Token present: {}", sessionId, token != null);

                    if (token != null && jwtService.validateToken(token)) {
                        try {
                            Claims claims = jwtService.getAllClaims(token);
                            String username = jwtService.getUsernameFromToken(token);
                            String userId = claims.get("userId", String.class);
                            String firstname = claims.get("firstName", String.class);
                            String lastname = claims.get("lastName", String.class);
                            String email = claims.get("email", String.class);
                            String phone = claims.get("phone", String.class);
                            List<String> claimRoles = claims.get("roles", List.class);
                            List<String> claimPermissions = claims.get("permissions", List.class);

                            Set<SimpleGrantedAuthority> roles = new HashSet<>();
                            if (claimRoles != null) {
                                claimRoles.forEach(role -> roles.add(new SimpleGrantedAuthority("ROLE_" + role)));
                            }
                            if (claimPermissions != null) {
                                claimPermissions.forEach(perm -> roles.add(new SimpleGrantedAuthority(perm)));
                            }

                            CustomUserDetails userDetails = new CustomUserDetails(
                                    token, userId, username, firstname, lastname, email, phone, roles
                            );
                            UsernamePasswordAuthenticationToken auth =
                                    new UsernamePasswordAuthenticationToken(userDetails, null, roles);

                            accessor.setUser(auth);
                            SecurityContextHolder.getContext().setAuthentication(auth);
                            sessionAuthMap.put(sessionId, auth);
                            log.info("Successfully authenticated user {} for session {}", username, sessionId);
                        } catch (Exception e) {
                            log.error("Error processing token for session {}: {}", sessionId, e.getMessage());
                            throw new InvalidJwtException("Error processing JWT token: " + e.getMessage());
                        }
                    } else {
                        log.error("Invalid or missing JWT token for session {}", sessionId);
                        throw new InvalidJwtException("Invalid or missing JWT token");
                    }
                } else if (command != null) { // SEND, SUBSCRIBE, etc.
                    Authentication existingAuth = sessionAuthMap.get(sessionId);
                    log.info("{} - Session {}, Existing auth: {}", command, sessionId, existingAuth != null ? existingAuth.getName() : "null");

                    if (existingAuth != null) {
                        accessor.setUser(existingAuth);
                        SecurityContextHolder.getContext().setAuthentication(existingAuth);
                        message = MessageBuilder.createMessage(message.getPayload(), accessor.getMessageHeaders());
                        log.info("Reattached authentication for session {} during {} command. User: {}",
                            sessionId, command, existingAuth.getName());
                    } else {
                        // Try to get authentication from SecurityContext as fallback
                        Authentication securityContextAuth = SecurityContextHolder.getContext().getAuthentication();
                        if (securityContextAuth != null && securityContextAuth.isAuthenticated()) {
                            accessor.setUser(securityContextAuth);
                            sessionAuthMap.put(sessionId, securityContextAuth);
                            message = MessageBuilder.createMessage(message.getPayload(), accessor.getMessageHeaders());
                            log.info("Retrieved authentication from SecurityContext for session {} during {} command. User: {}",
                                sessionId, command, securityContextAuth.getName());
                        } else {
                            log.error("No authentication found for session {} during {} command", sessionId, command);
                            throw new InvalidJwtException("No authentication found for session");
                        }
                    }
                }

                return message;
            }

            @Override
            public void afterSendCompletion(Message<?> message, MessageChannel channel, boolean sent, Exception ex) {
                StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
                if (StompCommand.DISCONNECT.equals(accessor.getCommand())) {
                    String sessionId = accessor.getSessionId();
                    sessionAuthMap.remove(sessionId);
                    log.info("Cleaned up session {} after disconnect", sessionId);
                }
            }
        });
    }

}