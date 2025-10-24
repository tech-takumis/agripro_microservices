package com.example.agriculture.service;

import com.example.agriculture.entity.RefreshToken;
import com.example.agriculture.exception.ApiException;
import com.example.agriculture.repository.RefreshTokenRepository;
import com.hashjosh.jwtshareable.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TokenRenewalService {

    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;

    public Map<String, String> refreshTokens(
            UUID userId, String oldRefreshToken, String username,
             Map<String,Object> claims, String clientIp, String userAgent,
            boolean rememberMe) {


        // 1️⃣ Validate old refresh token
        RefreshToken refreshToken = refreshTokenRepository.findByUserRefAndToken(String.valueOf(userId), oldRefreshToken)
                .orElseThrow(() -> ApiException.badRequest("Invalid refresh token"));


        refreshTokenRepository.delete(refreshToken);
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

        RefreshToken saveRefreshToken = RefreshToken.builder()
                .token(newRefreshToken)
                .userRef(String.valueOf(userId))
                .clientIp(clientIp)
                .userAgent(userAgent)
                .expiry(Instant.now())
                .build();

        refreshTokenRepository.save(saveRefreshToken);

        return Map.of(
                "accessToken", newAccessToken,
                "refreshToken", newRefreshToken
        );
    }
}

