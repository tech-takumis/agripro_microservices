package com.hashjosh.identity.repository;


import com.hashjosh.identity.entity.RefreshToken;
import com.hashjosh.identity.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    Optional<RefreshToken> findByToken(String token);
    List<RefreshToken> findAllByUser(User user);

    Boolean existsByToken(String token);
}
