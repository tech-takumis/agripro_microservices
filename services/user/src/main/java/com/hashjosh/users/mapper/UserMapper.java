package com.hashjosh.users.mapper;

import com.hashjosh.users.dto.AuthenticatedResponse;
import com.hashjosh.users.entity.Role;
import com.hashjosh.users.entity.User;
import com.hashjosh.users.wrapper.UserRegistrationRequestWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserMapper {

    private final PasswordEncoder passwordEncoder;

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

    public User toEntity(UserRegistrationRequestWrapper wrapper, Set<Role> roles) {
        return User.builder()
                .username(wrapper.request().getUsername())
                .password(passwordEncoder.encode(wrapper.request().getPassword()))
                .firstName(wrapper.request().getFirstName())
                .lastName(wrapper.request().getLastName())
                .email(wrapper.request().getEmail())
                .phoneNumber(wrapper.request().getPhoneNumber())
                .address(wrapper.request().getAddress())
                .tenantType(wrapper.tenantType())
                .roles(roles)
                .build();
    }
}
