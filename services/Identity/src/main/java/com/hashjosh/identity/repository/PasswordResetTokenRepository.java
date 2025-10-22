package com.hashjosh.identity.repository;

import com.hashjosh.identity.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, UUID> {
    Optional<PasswordResetToken> findByToken(String token);
}
