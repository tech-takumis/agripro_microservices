package com.hashjosh.users.mapper;

import com.hashjosh.users.entity.RefreshToken;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
public class RefreshTokenMapper {


    public RefreshToken toEntity(String refreshToken, UUID user, String clientIp, String userAgent, Long refreshExpiry) {
        return RefreshToken.builder()
                .token(refreshToken)
                .userAgent(userAgent)
                .userRef(user.toString())
                .clientIp(clientIp)
                .userRef(userAgent)
                .expiry(Instant.now().plusSeconds(refreshExpiry))
                .build();
    }
}
