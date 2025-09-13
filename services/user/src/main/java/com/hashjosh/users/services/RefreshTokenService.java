package com.hashjosh.users.services;

import com.hashjosh.users.mapper.RefreshTokenMapper;
import com.hashjosh.users.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final RefreshTokenMapper refreshTokenMapper;

    public void deleteByToken(String token) {
        refreshTokenRepository.deleteByToken(token);
    }
}
