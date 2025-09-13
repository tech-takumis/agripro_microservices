package com.hashjosh.users.repository;

import com.hashjosh.users.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;

@Repository
public interface   RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
    void deleteByUserRef(String userRef);
    void deleteByExpiryBefore(Instant time);
    void deleteByToken(String token);
}
