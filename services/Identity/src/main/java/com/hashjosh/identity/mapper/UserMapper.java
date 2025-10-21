package com.hashjosh.identity.mapper;

import com.hashjosh.constant.user.UserResponseDTO;
import com.hashjosh.identity.dto.UserRegistrationRequest;
import com.hashjosh.identity.entity.Tenant;
import com.hashjosh.identity.entity.User;
import com.hashjosh.identity.entity.UserAttribute;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserMapper {

    private final RoleMapper roleMapper;

    public User toUserEntity(UserRegistrationRequest dto, Tenant tenant, String passwordHash) {
        return User.builder()
                .tenant(tenant)
                .username(dto.getUsername())
                .email(dto.getEmail())
                .password(passwordHash)
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .active(true)
                .deleted(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public UserResponseDTO toUserResponseDto(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .tenant(user.getTenant().getKey())
                .active(user.isActive())
                .roles(user.getRoles().stream()
                                    .map( userRole -> roleMapper.toRoleResponseDto(userRole.getRole()))
                        .collect(Collectors.toList()))
                .build();
    }

}
