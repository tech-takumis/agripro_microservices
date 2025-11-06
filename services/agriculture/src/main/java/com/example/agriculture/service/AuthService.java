package com.example.agriculture.service;

import com.example.agriculture.config.CustomUserDetails;
import com.example.agriculture.dto.auth.LoginRequest;
import com.example.agriculture.dto.auth.LoginResponse;
import com.example.agriculture.dto.auth.RegistrationRequest;
import com.example.agriculture.entity.Agriculture;
import com.example.agriculture.entity.Permission;
import com.example.agriculture.entity.Role;
import com.example.agriculture.exception.ApiException;
import com.example.agriculture.kafka.AgricultureProducer;
import com.example.agriculture.mapper.AuthMapper;
import com.example.agriculture.mapper.UserMapper;
import com.example.agriculture.repository.RoleRepository;
import com.example.agriculture.repository.AgricultureRepository;
import com.hashjosh.jwtshareable.service.JwtService;
import com.hashjosh.kafkacommon.agriculture.AgricultureRegistrationContract;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AgricultureRepository agricultureRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final AgricultureProducer agricultureProducer;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final AuthMapper authMapper;

    @Transactional
    public Agriculture register(RegistrationRequest request) {
        if (agricultureRepository.existsByEmail(request.getEmail())) {
            throw ApiException.conflict("Email already in use: " + request.getEmail());
        }

        Set<Role> roles = new HashSet<>();
        request.getRoleNames().forEach(roleName -> {
            Role role = roleRepository.findByName(roleName)
                    .orElseThrow(() -> ApiException.notFound("Role not found: " + roleName));
            roles.add(role);
        });

        String username = generateSequentialUsername();

        Agriculture agriculture = userMapper.toUserEntity(request, roles, username);
        Agriculture registeredAgriculture = agricultureRepository.save(agriculture);

        AgricultureRegistrationContract agricultureRegistrationContract =
                AgricultureRegistrationContract.builder()
                        .userId(registeredAgriculture.getId())
                        .username(username)
                        .password(request.getPassword())
                        .firstName(registeredAgriculture.getFirstName())
                        .lastName(registeredAgriculture.getLastName())
                        .email(registeredAgriculture.getEmail())
                        .phoneNumber(registeredAgriculture.getPhoneNumber())
                        .build();

        agricultureProducer.publishEvent("agriculture-registration",agricultureRegistrationContract);

        return registeredAgriculture;
    }


    public LoginResponse login(LoginRequest request, HttpServletRequest httpRequest) {
        String clientIp = httpRequest.getRemoteAddr();
        String userAgent = httpRequest.getHeader("User-Agent");

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(auth);
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        Agriculture agriculture = getAgricultureWithRoles(userDetails.getUserId());

        Map<String, Object> claims = buildClaims(agriculture);

        String accessToken = jwtService.generateAccessToken(
                userDetails.getUsername(),
                claims,
                jwtService.getAccessTokenExpiry(request.isRememberMe())
        );

        String refreshToken = jwtService.generateRefreshToken(
                userDetails.getUsername(),
                clientIp,
                userAgent,
                jwtService.getRefreshTokenExpiry(request.isRememberMe())
        );

        String webSocketToken = jwtService.generateWebSocketToken(
                userDetails.getUsername(),
                claims
        );

        return authMapper.toAuthenticatedResponse(
                agriculture,
                webSocketToken,
                accessToken,
                refreshToken
        );
    }

    public Agriculture getAgricultureWithRoles(UUID userId) {
        return agricultureRepository.findByIdWithRolesAndPermissions(userId)
                .orElseThrow(() -> ApiException.notFound("User not found with ID: " + userId));
    }
    private Set<String> extractPermissions(Set<Role> roles) {
        return roles.stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(Permission::getName)
                .collect(Collectors.toSet());
    }

    private Set<String> extractRoles(Set<Role> roles) {
        return roles.stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
    }

    private String generateSequentialUsername() {
        // Fetch the last username from the database, ordered descending
        String lastUsername = agricultureRepository.findLastUsername();

        int nextNumber = 1;
        if (lastUsername != null && lastUsername.matches("100-\\d{3}")) {
            String numberPart = lastUsername.substring(4);
            nextNumber = Integer.parseInt(numberPart) + 1;
        }
        return String.format("100-%03d", nextNumber);
    }


    private String generateUniquePassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()";
        StringBuilder sb = new StringBuilder(12);
        for (int i = 0; i < 12; i++) {
            int idx = (int) (Math.random() * chars.length());
            sb.append(chars.charAt(idx));
        }
        return sb.toString();
    }


    private Map<String,Object> buildClaims(Agriculture agriculture) {

        // Extract permissions first
        Set<String> permissions = extractPermissions(agriculture.getRoles());

        // Convert roles to string names
        Set<String> roles = extractRoles(agriculture.getRoles());

        return Map.of(
                "userId", agriculture.getId(),
                "firstname", agriculture.getFirstName(),
                "lastname", agriculture.getLastName(),
                "email", agriculture.getEmail(),
                "phoneNumber", agriculture.getPhoneNumber(),
                "roles", roles,
                "permissions", permissions
        );
    }
}