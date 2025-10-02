package com.hashjosh.pcic.repository;

import com.hashjosh.pcic.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface   RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
    void deleteByUserRef(String userRef);
    void deleteByExpiryBefore(Instant time);
    void deleteByToken(String token);

    Optional<RefreshToken> findByToken(String token);
}
