package com.hashjosh.pcic.service;

import com.hashjosh.jwtshareable.service.JwtService;
import com.hashjosh.pcic.entity.RefreshToken;
import com.hashjosh.pcic.exception.ApiException;
import com.hashjosh.pcic.repository.RefreshTokenRepository;
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

        RefreshToken refreshToken = refreshTokenRepository.findByToken(oldRefreshToken)
                .orElseThrow(() -> ApiException.unauthorized("Invalid refresh token"));

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

        refreshTokenRepository.delete(refreshToken);
        RefreshToken newRefreshTokenEntity = RefreshToken.builder()
                .token(newRefreshToken)
                .userRef(String.valueOf(userId))
                .userAgent(userAgent)
                .clientIp(clientIp)
                .expiry(Instant.now().plusSeconds(jwtService.getRefreshTokenExpiry(rememberMe)))
                .build();
        refreshTokenRepository.save(newRefreshTokenEntity);

        return Map.of(
                "accessToken", newAccessToken,
                "refreshToken", newRefreshToken
        );
    }
}

