package com.hashjosh.jwtshareable.service;

import com.hashjosh.jwtshareable.properties.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

public class JwtService {
    private final SecretKey secretKey;
    private final JwtProperties jwtProperties;
    private RefreshTokenStore refreshTokenStore;

    public JwtService(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        this.secretKey = Keys.hmacShaKeyFor(
                jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    /** Optionally inject a store if you want to persist refresh token**/
    public void setRefreshToken(RefreshTokenStore refreshToken) {
        this.refreshTokenStore = refreshToken;
    }

    /** Generate refresh token**/
    public String generateRefreshToken(String username,String clientIp,String userAgent, Long expiryMillis) {
        String token = UUID.randomUUID().toString();
        if(refreshTokenStore != null){
            refreshTokenStore.save( token ,username, clientIp,userAgent, Instant.now().plusMillis(expiryMillis));
        }

        return token;
    }

    /**Generate Access token**/
    public String generateAccessToken(String subject,Map<String,Object> claims,
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

// Add these new methods to your existing JwtService class

/**
 * Validates if the token is valid and belongs to the given user
 */
public boolean isTokenValid(String token, UserDetails userDetails) {
    try {
        final String username = getUsernameFromToken(token);
        return username.equals(userDetails.getUsername()) && !isExpired(token);
    } catch (JwtException e) {
        return false;
    }
}

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

/**
 * Checks if token needs to be refreshed based on refresh threshold
 */
public boolean shouldTokenBeRefreshed(String token, long refreshThresholdMs) {
    try {
        final long remainingTime = getRemainingTimeInMillis(token);
        return remainingTime > 0 && remainingTime < refreshThresholdMs;
    } catch (JwtException e) {
        return false;
    }
}

/**
 * Validates refresh token against the store
 */
public boolean validateRefreshToken(String refreshToken, String clientIp, String userAgent) {
    if (refreshTokenStore == null) {
        return false;
    }
    return refreshTokenStore.validate(refreshToken, clientIp, userAgent);
}

/**
 * Invalidates a refresh token
 */
public void invalidateRefreshToken(String refreshToken) {
    if (refreshTokenStore != null) {
        refreshTokenStore.remove(refreshToken);
    }
}
}