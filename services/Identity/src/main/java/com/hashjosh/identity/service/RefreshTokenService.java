package com.hashjosh.identity.service;

import com.hashjosh.identity.entity.RefreshToken;
import com.hashjosh.identity.entity.User;
import com.hashjosh.identity.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    @Transactional
    public void deleteByToken(String token) {
        log.debug("Deleting refresh token: {}", token);
        refreshTokenRepository.findByToken(token)
                .ifPresent(refreshToken -> {
                    refreshTokenRepository.delete(refreshToken);
                    log.debug("Deleted refresh token: {}", token);
                });
    }

    public boolean validateRefreshToken(String refreshToken) {
         Optional<RefreshToken> refresh =  refreshTokenRepository.findByToken(refreshToken);

         if(refresh.isEmpty()) {
             log.debug("Refresh token not found: {}", refreshToken);
             return false;
         }

        return refresh.get().getExpiresAt().isAfter(LocalDateTime.now());
    }

    @Transactional
    public void saveRefreshToken(User user, String newRefreshToken, String clientIp, String userAgent) {
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(newRefreshToken)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusDays(30)) // Example: token valid for 30 days
                .userAgent(userAgent)
                .build();

        refreshTokenRepository.save(refreshToken);
        log.debug("Saved new refresh token for user: {}", user.getUsername());
    }
}
