package com.hashjosh.gatewayservice.config;

import com.hashjosh.gatewayservice.dto.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

@Component
@Profile("gateway_security")
@RequiredArgsConstructor
public class JwtUtil {

    private final JwtProperties jwtProperties;
    SecretKey secretKey;

    @PostConstruct
    public void init() {
        this.secretKey = Keys.hmacShaKeyFor(jwtProperties.secret().getBytes());
    }

    public boolean validateToken(String token) {
        try{
            Claims claims = getAllClaimsFromToken(token);
            return claims != null && claims.getSubject() != null;
        }catch (Exception e) {
            return false;
        }
    }
    public Claims getAllClaimsFromToken(String token) {
        return  Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

}
