package com.hashjosh.identity.service;

import com.hashjosh.constant.user.UserResponseDTO;
import com.hashjosh.identity.exception.ApiException;
import com.hashjosh.identity.mapper.UserMapper;
import com.hashjosh.identity.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    public List<UserResponseDTO> findAll(String tenantKey) {

        if (tenantKey != null && !tenantKey.isBlank()) {
            return userRepository.findAllUsersByTenantKeyIgnoreCase(tenantKey)
                    .stream()
                    .map(userMapper::toUserResponseDto)
                    .toList();
        }

        return userRepository.findAll().stream().map(userMapper::toUserResponseDto).toList();
    }

    @Transactional(readOnly = true)
    public UserResponseDTO findById(UUID id) {
        return userRepository.findById(id)
                .map(userMapper::toUserResponseDto)
                .orElseThrow(() -> ApiException.notFound("User not found"));
    }

}
