package com.hashjosh.identity.service;

import com.hashjosh.identity.entity.Permission;
import com.hashjosh.identity.entity.Role;
import com.hashjosh.identity.entity.User;
import com.hashjosh.identity.properties.JwtProperties;
import com.hashjosh.identity.repository.RefreshTokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtService {
    private final JwtProperties jwtProperties;
    private final SecretKey secretKey;

    public JwtService(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        this.secretKey = Keys.hmacShaKeyFor(
                jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    public String generateAuthToken(User user, Long expiryMillis) {

        Map<String,Object> claims = new HashMap<>();
        claims.put("userId", user.getId().toString());
        claims.put("username", user.getUsername());
        claims.put("tenantId", user.getTenant().getId().toString());
        claims.put("email", user.getEmail());
        claims.put("roles", user.getRoles().stream().map(
                Role::getName
        ).toList());
        claims.put("permissions", user.getPermissions().stream().map(
                Permission::getName
        ).toList());


        return Jwts.builder()
                .subject(user.getUsername())
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plusMillis(expiryMillis)))
                .claims(claims)
                .signWith(secretKey)
                .compact();

    }

    public String generateSecureRefreshToken() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] tokenBytes = new byte[64];
        secureRandom.nextBytes(tokenBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
    }

    /**Generate Access token**/
    public Claims getAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

    }


    public boolean isExpired(String token) {
        try {
            return getAllClaims(token).getExpiration().toInstant().isBefore(Instant.now());
        } catch (ExpiredJwtException ex) {
            return true;
        }
    }

    /**
     * Validate the JWT token (signature and expiration)
     */
    public boolean validateToken(String token) {
        try {
           getAllClaims(token).getSubject();
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

}