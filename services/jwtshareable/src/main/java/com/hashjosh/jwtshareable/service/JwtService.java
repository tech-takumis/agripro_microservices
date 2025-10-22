package com.hashjosh.jwtshareable.service;

import com.hashjosh.jwtshareable.properties.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

public class JwtService {
    private final SecretKey secretKey;
    private final JwtProperties jwtProperties;

    public JwtService(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        this.secretKey = Keys.hmacShaKeyFor(
                jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
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
            return  true;
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

    public Long getAccessTokenExpiry(boolean rememberMe){
        return rememberMe
                ? jwtProperties.getAccessTokenExpirationRememberMeMs()
                : jwtProperties.getAccessTokenExpirationMs();
    }

    public Long getRefreshTokenExpiry(boolean rememberMe){
        return rememberMe
                ? jwtProperties.getRefreshTokenExpirationRememberMeMs()
                : jwtProperties.getRefreshTokenExpirationMs();
    }

    public Long getWebSocketTokenExpiry(){
        return jwtProperties.getWebSocketExpirationMs();
    }
// Add these new methods to your existing JwtService class


/**
 * Extracts expiration date from token
 */
public Date getExpirationDateFromToken(String token) {
    return getAllClaims(token).getExpiration();
}

/**
 * Calculate remaining validity time in milliseconds
 */
public long getRemainingTimeInMillis(String token) {
    try {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.getTime() - System.currentTimeMillis();
    } catch (JwtException e) {
        return -1;
    }
}

}