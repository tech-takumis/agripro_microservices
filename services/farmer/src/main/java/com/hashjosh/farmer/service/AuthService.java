package com.hashjosh.farmer.service;

import com.hashjosh.farmer.config.CustomUserDetails;
import com.hashjosh.farmer.dto.*;
import com.hashjosh.farmer.entity.*;
import com.hashjosh.farmer.exception.ApiException;
import com.hashjosh.farmer.kafka.FarmerProducer;
import com.hashjosh.farmer.mapper.UserMapper;
import com.hashjosh.farmer.repository.*;
import com.hashjosh.jwtshareable.service.JwtService;
import com.hashjosh.kafkacommon.farmer.FarmerRegistrationContract;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final FarmerRepository farmerRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final FarmerProducer farmerProducer;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    @Transactional
    public Farmer register(RegistrationRequest request) {

        if (farmerRepository.existsByEmail(request.getEmail())) {
            throw ApiException.badRequest("Email already exists");
        }

        if (farmerRepository.existsByUsername(request.getRsbsaId())) {
            throw ApiException.badRequest("RSBSA ID already exists");
        }

        Set<Role> roles = Collections.singleton(roleRepository.findByName("FARMER")
                .orElseThrow(() -> ApiException.badRequest("Role FARMER not found")));

        // Create and save UserProfile first
        Farmer farmer = userMapper.toUserEntity(request, roles);

        // Verify password was properly encoded
        if (farmer.getPassword() == null || farmer.getPassword().trim().isEmpty()) {
            throw new RuntimeException("Password encoding failed");
        }

        // Save user (will cascade save the profile)
        Farmer registeredFarmer = farmerRepository.save(farmer);

        log.info("Farmer registered successfully. Username: {}, Has password: {}",
                registeredFarmer.getUsername(),
                registeredFarmer.getPassword() != null && !registeredFarmer.getPassword().isEmpty());

        publishUserRegistrationEvent(request, registeredFarmer);

        return registeredFarmer;
    }

    private void publishUserRegistrationEvent(RegistrationRequest request, Farmer savedFarmer) {
        FarmerRegistrationContract farmerRegistrationContract =
                FarmerRegistrationContract.builder()
                        .userId(savedFarmer.getId())
                        .username(savedFarmer.getUsername())
                        .password(request.getPassword())
                        .firstName(savedFarmer.getFirstName())
                        .lastName(savedFarmer.getLastName())
                        .email(savedFarmer.getEmail())
                        .phoneNumber(savedFarmer.getPhoneNumber())
                        .rsbsaId(request.getRsbsaId())
                        .build();

        farmerProducer.publishFarmerRegistrationEvent(farmerRegistrationContract);
    }

    public AuthenticatedResponse login(LoginRequest request, String clientIp, String userAgent) {
        try {
            log.info("Attempting login for user: {}", request.getUsername());

            // Verify password in request
            if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
                throw new RuntimeException("Password cannot be empty");
            }

            // Log the user exists and has password (without showing the password)
            Farmer farmer = farmerRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> ApiException.badRequest("User not found"));

            log.info("Found user: {}, Has password: {}",
                    farmer.getUsername(),
                    farmer.getPassword() != null && !farmer.getPassword().isEmpty());

            // Authenticate user
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(auth);

            CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
            Farmer farmerLoggedIn = userDetails.getFarmer();

            if (farmerLoggedIn == null) {
                throw ApiException.badRequest("User not found");
            }

            // Extract permissions and roles
            Set<String> permissions = farmerLoggedIn.getRoles().stream()
                    .flatMap(role -> role.getPermissions().stream())
                    .map(Permission::getName)
                    .collect(Collectors.toSet());

            Set<String> roleNames = farmerLoggedIn.getRoles().stream()
                    .map(Role::getName)
                    .collect(Collectors.toSet());

            // Jwt claims
            Map<String, Object> claims = Map.of(
                    "userId", farmerLoggedIn.getId().toString(), // Ensure userId is a string
                    "firstname", farmerLoggedIn.getFirstName(),
                    "lastname", farmerLoggedIn.getLastName(),
                    "email", farmerLoggedIn.getEmail(),
                    "phoneNumber", farmerLoggedIn.getPhoneNumber(),
                    "roles", roleNames,
                    "permissions", permissions
            );

            // Generate tokens
            String accessToken = jwtService.generateAccessToken(
                    farmerLoggedIn.getUsername(),
                    claims,
                    jwtService.getAccessTokenExpiry(request.isRememberMe())
            );

            String refreshToken = jwtService.generateRefreshToken(
                    farmerLoggedIn.getUsername(), clientIp, userAgent,
                    jwtService.getRefreshTokenExpiry(request.isRememberMe())
            );

            String webSocketToken = jwtService.generateWebSocketToken(farmerLoggedIn.getUsername(),claims);

            return userMapper.toAuthenticatedResponse(farmerLoggedIn, accessToken, refreshToken,webSocketToken);
        } catch (Exception e) {
            log.error("Login failed for user {}: {}", request.getUsername(), e.getMessage());
            throw new RuntimeException("Authentication failed: " + e.getMessage());
        }
    }

    public AuthUserResponse getAuthenticatedUser(UUID id) {
        Farmer farmer = farmerRepository.findById(id)
                .orElseThrow(() -> ApiException.badRequest("User not found"));

        return userMapper.toAuthUserResponse(farmer);
    }
}