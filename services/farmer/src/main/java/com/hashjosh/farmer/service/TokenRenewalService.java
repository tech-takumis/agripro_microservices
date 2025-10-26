package com.hashjosh.farmer.service;

import com.hashjosh.farmer.entity.RefreshToken;
import com.hashjosh.farmer.exception.ApiException;
import com.hashjosh.farmer.repository.RefreshTokenRepository;
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

        // 3️⃣ Save new refresh token
        RefreshToken newRefreshTokenEntity = RefreshToken.builder()
                .token(newRefreshToken)
                .userRef(String.valueOf(userId))
                .clientIp(clientIp)
                .userAgent(userAgent)
                .expiry(Instant.now().plusSeconds(jwtService.getRefreshTokenExpiry(rememberMe)))
                .build();
        refreshTokenRepository.save(newRefreshTokenEntity);
        return Map.of(
                "accessToken", newAccessToken,
                "refreshToken", newRefreshToken
        );
    }
}

