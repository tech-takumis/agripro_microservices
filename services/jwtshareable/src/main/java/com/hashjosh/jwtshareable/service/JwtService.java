package com.hashjosh.jwtshareable.service;

import com.hashjosh.jwtshareable.properties.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

public class JwtService {
    private final SecretKey secretKey;
    private final JwtProperties jwtProperties;

    public JwtService(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        this.secretKey = Keys.hmacShaKeyFor(
                jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Generate WebSocket token with claims and webSocketExpirationMs expiry
     */
    public String generateWebSocketToken(String subject, Map<String, Object> claims) {
        return Jwts.builder()
                .subject(subject)
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plusMillis(getWebSocketTokenExpiry())))
                .claims(claims)
                .signWith(secretKey)
                .compact();
    }

    /**
     * Generate Access token
     **/
    public String generateAccessToken(String subject, Map<String, Object> claims,
                                      long expiryMillis) {

        return Jwts.builder()
                .subject(subject)
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plusMillis(expiryMillis)))
                .claims(claims)
                .signWith(secretKey)
                .compact();

    }

    public Claims getAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String getUsernameFromToken(String token) {
        return getAllClaims(token).getSubject();
    }

    public Claims getClaimsAllowExpired(String token) {
        try {
            return getAllClaims(token);
        } catch (ExpiredJwtException ex) {
            return ex.getClaims();
        }
    }

    public boolean validateToken(String token) {
        try {
            getAllClaims(token).getSubject();
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    public boolean isExpired(String token) {
        try {
            return getAllClaims(token).getExpiration().toInstant().isBefore(Instant.now());
        } catch (io.jsonwebtoken.ExpiredJwtException ex) {
            return true;
        }
    }

    public Long getAccessTokenExpiry(boolean rememberMe) {
        return rememberMe
                ? jwtProperties.getAccessTokenExpirationRememberMeMs()
                : jwtProperties.getAccessTokenExpirationMs();
    }

    public Long getRefreshTokenExpiry(boolean rememberMe) {
        return rememberMe
                ? jwtProperties.getRefreshTokenExpirationRememberMeMs()
                : jwtProperties.getRefreshTokenExpirationMs();
    }

    public Long getWebSocketTokenExpiry() {
        return jwtProperties.getWebSocketExpirationMs();
    }

    /**
     * Generate a secure, random refresh token string.
     * The token is opaque and should be stored in the database with metadata for validation.
     */
    public String generateRefreshToken(String username, String clientIp, String userAgent, long expiryMillis) {
        // Use SecureRandom for cryptographic strength
        byte[] randomBytes = new byte[64];
        new SecureRandom().nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

}