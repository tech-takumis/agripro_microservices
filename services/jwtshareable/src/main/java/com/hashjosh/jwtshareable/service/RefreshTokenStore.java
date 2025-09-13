package com.hashjosh.jwtshareable.service;

import java.time.Instant;
import java.util.Optional;

public interface RefreshTokenStore {
    void save(String token, Object userRef, String clientIp, String userAgent, Instant expiry);
    boolean validate(String token, String clientIp, String userAgent);
    void remove(String token);
    Optional<Object> getUserRef(String token);
    void removeAllUserTokens(Object userRef);
    void removeExpiredTokens();
}