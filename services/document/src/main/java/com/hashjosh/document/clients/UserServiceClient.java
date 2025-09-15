package com.hashjosh.document.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceClient {
    public UserResponse getUserById(UUID uuid, String token) {
        return null;
    }
}
