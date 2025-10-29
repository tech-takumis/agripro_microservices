package com.hashjosh.pcic.service;


import com.hashjosh.jwtshareable.service.JwtService;
import com.hashjosh.kafkacommon.pcic.PcicRegistrationContract;
import com.hashjosh.pcic.config.CustomUserDetails;
import com.hashjosh.pcic.dto.auth.AuthenticatedUser;
import com.hashjosh.pcic.dto.auth.LoginRequest;
import com.hashjosh.pcic.dto.auth.LoginResponse;
import com.hashjosh.pcic.dto.auth.RegistrationRequest;
import com.hashjosh.pcic.entity.Pcic;
import com.hashjosh.pcic.entity.Permission;
import com.hashjosh.pcic.entity.Role;
import com.hashjosh.pcic.exception.ApiException;
import com.hashjosh.pcic.kafka.KafkaProducer;
import com.hashjosh.pcic.mapper.UserMapper;
import com.hashjosh.pcic.repository.PcicRepository;
import com.hashjosh.pcic.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final PcicRepository pcicRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final KafkaProducer pcicProducer;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public Pcic register(RegistrationRequest request) {
        if (pcicRepository.existsByEmail(request.getEmail())) {
            throw ApiException.conflict("Email already exists");
        }

        Set<Role> roles = new HashSet<>();
        request.getRolesId().forEach(roleId -> {
            Role role = roleRepository.findById(roleId)
                    .orElseThrow(() -> ApiException.notFound("Role not found"));
            roles.add(role);
        });

        Pcic pcic = userMapper.toUserEntity(request, roles);
        Pcic registeredPcic = pcicRepository.save(pcic);

        publishUserRegistrationEvent(request, pcic);

        return registeredPcic;
    }

    private void publishUserRegistrationEvent(RegistrationRequest request, Pcic savedPcic) {
        PcicRegistrationContract contract =
                PcicRegistrationContract.builder()
                        .userId(savedPcic.getId())
                        .username(savedPcic.getUsername())
                        .password(request.getPassword())
                        .email(savedPcic.getEmail())
                        .firstName(savedPcic.getFirstName())
                        .lastName(savedPcic.getLastName())
                        .phoneNumber(savedPcic.getPhoneNumber())
                        .build();

        pcicProducer.publishEvent("pcic-events",contract);
    }

    public LoginResponse login(LoginRequest request, String clientIp, String userAgent) {
        // ✅ authenticate user
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(auth);
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();

        Pcic pcic = getPcicWithRoles(userDetails.getUserId());

        Map<String,Object> claims = buildClaims(pcic);

        // ✅ generate tokens (do NOT call login or authenticate again)
        String accessToken = jwtService.generateAccessToken(
                pcic.getUsername(),
                claims,
                jwtService.getAccessTokenExpiry(request.isRememberMe())
        );

        String refreshToken = jwtService.generateRefreshToken(
                pcic.getUsername(), clientIp, userAgent,
                jwtService.getRefreshTokenExpiry(request.isRememberMe())
        );

        String webSocketToken = jwtService.generateWebSocketToken(
                pcic.getUsername(),
                claims
        );

        AuthenticatedUser response = userMapper.toAuthenticatedResponse(pcic);

        return new LoginResponse(accessToken, refreshToken, webSocketToken, response);
    }

    @Transactional(readOnly = true)
    public AuthenticatedUser getAuthenticatedUser() {

        var Authentication = SecurityContextHolder.getContext().getAuthentication();
        if (Authentication == null || !Authentication.isAuthenticated()) {
            throw ApiException.unauthorized("User is not authenticated");
        }

        Object principal = Authentication.getPrincipal();
        if (!(principal instanceof CustomUserDetails userDetails)) {
            throw ApiException.unauthorized("User is not authenticated");
        }
        Pcic pcic = pcicRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> ApiException.notFound("User not found"));

        return userMapper.toAuthenticatedResponse(pcic);
    }

    public Pcic getPcicWithRoles(UUID userId) {
        return pcicRepository.findByIdWithRolesAndPermissions(userId)
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

    private Map<String,Object> buildClaims(Pcic pcic) {

        // Extract permissions first
        Set<String> permissions = extractPermissions(pcic.getRoles());

        // Convert roles to string names
        Set<String> roles = extractRoles(pcic.getRoles());

        return Map.of(
                "userId", pcic.getId(),
                "firstname", pcic.getFirstName(),
                "lastname", pcic.getLastName(),
                "email", pcic.getEmail(),
                "phoneNumber", pcic.getPhoneNumber(),
                "roles", roles,
                "permissions", permissions
        );
    }
}
