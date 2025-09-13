package com.hashjosh.users.services;

import com.hashjosh.jwtshareable.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TokenRenewalService {

    private final JwtService jwtService;

    public Map<String, String> refreshTokens(
            UUID userId, String oldRefreshToken, String username,
            String tenantId, String role, String clientIp, String userAgent,
            boolean rememberMe) {

        // 1️⃣ Invalidate old refresh token
        jwtService.invalidateRefreshToken(oldRefreshToken);
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);

        // 2️⃣ Generate new tokens
        String newAccessToken = jwtService.generateAccessToken(
                userId,
                username,
                tenantId,
                claims,
                jwtService.getAccessTokenExpiry(rememberMe)
        );

        String newRefreshToken = jwtService.generateRefreshToken(
                username, clientIp, userAgent,
                jwtService.getRefreshTokenExpiry(rememberMe)
        );

        return Map.of(
                "accessToken", newAccessToken,
                "refreshToken", newRefreshToken
        );
    }
}

