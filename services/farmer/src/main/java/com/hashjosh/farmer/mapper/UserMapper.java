package com.hashjosh.farmer.mapper;

import com.hashjosh.farmer.dto.AuthenticatedResponse;
import com.hashjosh.farmer.dto.RegistrationRequest;
import com.hashjosh.farmer.entity.Role;
import com.hashjosh.farmer.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class UserMapper {

    private final PasswordEncoder passwordEncoder;

    public User toUserEntity(
            RegistrationRequest request, Set<Role> roles) {
        return User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .address(request.getAddress())
                .roles(roles)
                .build();
    }

    public AuthenticatedResponse toAuthenticatedResponse(User user) {
        Set<String> roles = new HashSet<>();
        Set<String> permissions = new HashSet<>();

        // roles and permissions are now initialized
        user.getRoles().forEach(role -> {
            roles.add(role.getName());
            role.getPermissions().forEach(permission -> permissions.add(permission.getName()));
        });


        return new AuthenticatedResponse(
                user.getId(),
                user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getAddress(),
                roles,
                permissions
        );
    }
}
