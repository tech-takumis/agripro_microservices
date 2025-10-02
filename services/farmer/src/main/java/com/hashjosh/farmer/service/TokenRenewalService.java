package com.hashjosh.farmer.service;

import com.hashjosh.jwtshareable.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TokenRenewalService {

    private final JwtService jwtService;

    public Map<String, String> refreshTokens(
            UUID userId, String oldRefreshToken, String username,
            String tenantId, Map<String,Object> claims, String clientIp, String userAgent,
            boolean rememberMe) {

        // 1️⃣ Invalidate old refresh token
        jwtService.invalidateRefreshToken(oldRefreshToken);

        // 2️⃣ Generate new tokens
        String newAccessToken = jwtService.generateAccessToken(
                username,
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

