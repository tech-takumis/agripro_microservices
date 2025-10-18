package com.example.agriculture.mapper;

import com.example.agriculture.dto.auth.AuthUser;
import com.example.agriculture.dto.auth.AuthenticatedResponse;
import com.example.agriculture.dto.auth.LoginResponse;
import com.example.agriculture.dto.rbac.RoleResponse;
import com.example.agriculture.entity.Agriculture;
import com.example.agriculture.entity.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AuthMapper {

    private final RoleMapper roleMapper;
    public LoginResponse toAuthenticatedResponse(Agriculture agriculture, String webSocketToken, String accessToken, String refreshToken) {
        AuthUser authUser = toAuthUser(agriculture);

        return LoginResponse.builder()
                .webSocketToken(webSocketToken)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(authUser)
                .build();
    }

    public AuthUser toAuthUser(Agriculture agriculture) {
        Set<RoleResponse> roles = agriculture.getRoles().stream()
                .map(roleMapper::toRoleResponse)
                .collect(Collectors.toSet());
        return AuthUser.builder()
                .id(agriculture.getId())
                .username(agriculture.getUsername())
                .email(agriculture.getEmail())
                .firstName(agriculture.getFirstName())
                .lastName(agriculture.getLastName())
                .phoneNumber(agriculture.getPhoneNumber())
                .roles(roles)
                .build();
    }
}
