package com.hashjosh.users.services;

import com.hashjosh.kafkacommon.user.TenantType;
import com.hashjosh.users.dto.AuthenticatedResponse;
import com.hashjosh.users.dto.RegistrationResponse;
import com.hashjosh.users.dto.UserResponse;
import com.hashjosh.users.entity.User;
import com.hashjosh.users.exception.UserException;
import com.hashjosh.users.mapper.UserMapper;
import com.hashjosh.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StaffService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public List<User> getUsersByType(TenantType tenantType) {
        return userRepository.findByTenantType(tenantType);
    }


    public UserResponse getUserById(UUID userId) {
        User user =  userRepository.findById(userId)
                .orElseThrow(() -> new UserException(
                        "User id "+userId+ " not found!",
                        HttpStatus.NOT_FOUND.value()
                ));

        return userMapper.toUserResponse(user);
    }

    public List<UserResponse> findAll() {
        return userRepository.findAll().stream()
                .map(userMapper::toUserResponse)
                .collect(Collectors.toList());
    }
}
