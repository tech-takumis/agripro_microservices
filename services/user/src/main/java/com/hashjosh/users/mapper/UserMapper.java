package com.hashjosh.users.mapper;

import com.hashjosh.users.dto.AuthenticatedResponse;
import com.hashjosh.users.dto.UserResponse;
import com.hashjosh.users.dto.permission.PermissionResponse;
import com.hashjosh.users.dto.role.RoleResponse;
import com.hashjosh.users.entity.Role;
import com.hashjosh.users.entity.User;
import com.hashjosh.users.wrapper.UserRegistrationRequestWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserMapper {

    private final PasswordEncoder passwordEncoder;
    private final RoleMapper roleMapper;
    private final PermissionMapper permissionMapper;

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

    public UserResponse toUserResponse(User user) {

        // Get roles and permissions for user
        Set<RoleResponse> roles = new HashSet<>();

        user.getRoles().forEach(role -> {
            // Get the permissions for the role
            RoleResponse roleResponse = roleMapper.toRoleResponse(role);
            List<PermissionResponse> permissionResponses = new ArrayList<>();
            role.getPermissions().forEach(permission -> {
                permissionResponses.add(roleMapper.toPermissionResponse(permission));
            });
            roleResponse.setPermissions(permissionResponses);
            roles.add(roleResponse);
        });

        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .address(user.getAddress())
                .roles(roles)
                .build();
    }
}
