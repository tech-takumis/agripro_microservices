package com.example.agriculture.service;

import com.example.agriculture.config.CustomUserDetails;
import com.example.agriculture.dto.AuthenticatedResponse;
import com.example.agriculture.dto.LoginRequest;
import com.example.agriculture.dto.LoginResponse;
import com.example.agriculture.dto.RegistrationRequest;
import com.example.agriculture.entity.Agriculture;
import com.example.agriculture.entity.Permission;
import com.example.agriculture.entity.Role;
import com.example.agriculture.exception.UserException;
import com.example.agriculture.kafka.AgricultureProducer;
import com.example.agriculture.mapper.UserMapper;
import com.example.agriculture.repository.RoleRepository;
import com.example.agriculture.repository.AgricultureRepository;
import com.hashjosh.jwtshareable.service.JwtService;
import com.hashjosh.kafkacommon.agriculture.AgricultureRegistrationContract;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
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

    @Transactional
    public Agriculture register(RegistrationRequest request) {
        if (agricultureRepository.existsByEmail(request.getEmail())) {
            throw new UserException("Email already exists", HttpStatus.BAD_REQUEST.value());
        }

        Set<Role> roles = new HashSet<>();
        request.getRolesId().forEach(roleId -> {
            Role role = roleRepository.findById(roleId)
                    .orElseThrow(() -> new UserException("Role not found", HttpStatus.NOT_FOUND.value()));
            roles.add(role);
        });

        Agriculture agriculture = userMapper.toUserEntity(request, roles);
        Agriculture registeredAgriculture = agricultureRepository.save(agriculture);

        publishUserRegistrationEvent(request, agriculture);

        return registeredAgriculture;
    }

    private void publishUserRegistrationEvent(RegistrationRequest request, Agriculture savedAgriculture) {
        AgricultureRegistrationContract agricultureRegistrationContract =
                AgricultureRegistrationContract.builder()
                        .userId(savedAgriculture.getId())
                        .username(savedAgriculture.getUsername())
                        .password(request.getPassword())
                        .firstName(savedAgriculture.getFirstName())
                        .lastName(savedAgriculture.getLastName())
                        .email(savedAgriculture.getEmail())
                        .phoneNumber(savedAgriculture.getPhoneNumber())
                        .build();

        agricultureProducer.publishAgricultureRegistrationEvent(agricultureRegistrationContract);
    }

    public LoginResponse login(LoginRequest request, String clientIp, String userAgent) {
        // ✅ authenticate user
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        Agriculture agriculture = userDetails.getAgriculture();

        // Extract permissions first
        Set<String> permissions = agriculture.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(Permission::getName)
                .collect(Collectors.toSet());

        // Convert roles to string names
        Set<String> roleNames = agriculture.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        // Jwt claims for user
        Map<String, Object> claims = Map.of(
                "userId", agriculture.getId(),
                "firstname", agriculture.getFirstName(),
                "lastname", agriculture.getLastName(),
                "email", agriculture.getEmail(),
                "phoneNumber", agriculture.getPhoneNumber(),
                "roles", roleNames,
                "permissions", permissions
        );

        // ✅ generate tokens (do NOT call login or authenticate again)
        String accessToken = jwtService.generateAccessToken(
                agriculture.getUsername(),
                claims,
                jwtService.getAccessTokenExpiry(request.isRememberMe())
        );

        String refreshToken = jwtService.generateRefreshToken(
                agriculture.getUsername(), clientIp, userAgent,
                jwtService.getRefreshTokenExpiry(request.isRememberMe())
        );

        return new LoginResponse(accessToken, refreshToken);
    }

    @Transactional(readOnly = true)
    public AuthenticatedResponse getAuthenticatedUser(Agriculture request) {

        Agriculture agriculture = agricultureRepository.findByIdWithRolesAndPermissions(request.getId())
                .orElseThrow(() -> new UserException("User not found", HttpStatus.NOT_FOUND.value()));

        return userMapper.toAuthenticatedResponse(agriculture);
    }
}