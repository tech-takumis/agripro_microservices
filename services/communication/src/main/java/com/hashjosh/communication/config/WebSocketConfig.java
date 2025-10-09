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
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.*;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
@Slf4j
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtService  jwtService;
    private static final String TOKEN_HEADER = "X-Auth-Token";

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry){
        registry.enableSimpleBroker("/topic","/user");
        registry.setApplicationDestinationPrefixes("/app");
        registry.setUserDestinationPrefix("/user");
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
    public  void configureClientInboundChannel(ChannelRegistration registration){
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    // Extract token from session attributes (placed there by HandshakeInterceptor)
                    String token = (String) Objects.requireNonNull(accessor.getSessionAttributes()).get(TOKEN_HEADER);
                    log.info("Received token from client inbound channel: {}", token);

                    if (token != null && jwtService.validateToken(token)) {
                        Claims claims = jwtService.getAllClaims(token);
                        String username = jwtService.getUsernameFromToken(token);
                        String userId = claims.get("userId",String.class);
                        String firstname = claims.get("firstName",String.class);
                        String lastname = claims.get("lastName",String.class);
                        String email = claims.get("email",String.class);
                        String phone = claims.get("phone",String.class);
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
                        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                                userDetails, null, roles
                        );
                        accessor.setUser(auth);
                        SecurityContextHolder.getContext().setAuthentication(auth);
                    } else {
                        log.error("WebSocket connection failed: Invalid or missing token.");
                        throw new InvalidJwtException("Invalid or missing JWT token");
                    }
                }
                return message;
            }
        });
    }
}