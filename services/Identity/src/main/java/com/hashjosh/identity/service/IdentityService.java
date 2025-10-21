package com.hashjosh.identity.service;

import com.hashjosh.constant.user.UserResponseDTO;
import com.hashjosh.identity.mapper.UserMapper;
import com.hashjosh.identity.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class IdentityService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public List<UserResponseDTO> findAll() {
        return userRepository.findAll().stream().map(userMapper::toUserResponseDto).toList();
    }
}
