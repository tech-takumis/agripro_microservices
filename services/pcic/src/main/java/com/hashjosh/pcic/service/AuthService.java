package com.hashjosh.pcic.service;


import com.hashjosh.jwtshareable.service.JwtService;
import com.hashjosh.kafkacommon.agriculture.AgricultureRegistrationContract;
import com.hashjosh.kafkacommon.pcic.PcicRegistrationContract;
import com.hashjosh.pcic.config.CustomUserDetails;
import com.hashjosh.pcic.dto.*;
import com.hashjosh.pcic.entity.*;
import com.hashjosh.pcic.exception.ApiException;
import com.hashjosh.pcic.kafka.PcicProducer;
import com.hashjosh.pcic.mapper.UserMapper;
import com.hashjosh.pcic.repository.*;
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

    private final PcicRepository pcicRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PcicProducer pcicProducer;
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

    public LoginResponse login(LoginRequest request,String clientIp, String userAgent) {
        // ✅ authenticate user
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        Pcic pcic = userDetails.getPcic();

        // Extract permissions first
        Set<String> permissions = pcic.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(Permission::getName)
                .collect(Collectors.toSet());

        // Convert roles to string names
        Set<String> roleNames = pcic.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        // Jwt claims for user
        Map<String, Object> claims = Map.of(
                "userId", pcic.getId(),
                "firstname", pcic.getFirstName(),
                "lastname", pcic.getLastName(),
                "email", pcic.getEmail(),
                "phoneNumber", pcic.getPhoneNumber(),
                "roles", roleNames,
                "permissions", permissions
        );

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

        return new LoginResponse(accessToken, refreshToken);
    }

    @Transactional(readOnly = true)
    public AuthenticatedResponse getAuthenticatedUser(Pcic request) {

        Pcic pcic = pcicRepository.findByIdWithRolesAndPermissions(request.getId())
                .orElseThrow(() -> ApiException.notFound("User not found"));

        return userMapper.toAuthenticatedResponse(pcic);
    }
}
