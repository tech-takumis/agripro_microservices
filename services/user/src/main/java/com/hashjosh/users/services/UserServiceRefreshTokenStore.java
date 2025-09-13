package com.hashjosh.users.services;

import com.hashjosh.jwtshareable.service.RefreshTokenStore;
import com.hashjosh.users.entity.RefreshToken;
import com.hashjosh.users.repository.RefreshTokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceRefreshTokenStore implements RefreshTokenStore {

    private final RefreshTokenRepository repository;

    @Override
    public void save(String token, Object userRef, String clientIp, String userAgent, Instant expiry) {
        RefreshToken refreshToken = RefreshToken.builder()
                .token(token)
                .userRef(userRef.toString())
                .clientIp(clientIp)
                .userAgent(userAgent)
                .expiry(expiry)
                .createdAt(Instant.now())
                .build();

        repository.save(refreshToken);
    }

    @Override
    public boolean validate(String token, String clientIp, String userAgent) {
        return repository.findById(token)
                .map(rt -> {
                    boolean isValid = !rt.getExpiry().isBefore(Instant.now()) &&
                            rt.getClientIp().equals(clientIp) &&
                            rt.getUserAgent().equals(userAgent);

                    if (!isValid) {
                        repository.deleteById(token);
                    }

                    return isValid;
                })
                .orElse(false);
    }

    @Override
    public void remove(String token) {
        repository.deleteById(token);
    }

    @Override
    public Optional<Object> getUserRef(String token) {
        return repository.findById(token)
                .map(RefreshToken::getUserRef);
    }

    @Override
    public void removeAllUserTokens(Object userRef) {
        repository.deleteByUserRef(userRef.toString());
    }

    @Override
    public void removeExpiredTokens() {
        repository.deleteByExpiryBefore(Instant.now());
    }
}

