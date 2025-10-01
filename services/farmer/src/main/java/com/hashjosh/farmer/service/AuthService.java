package com.hashjosh.farmer.service;

import com.hashjosh.farmer.config.CustomUserDetails;
import com.hashjosh.farmer.dto.*;
import com.hashjosh.farmer.entity.*;
import com.hashjosh.farmer.exception.UserException;
import com.hashjosh.farmer.kafka.FarmerProducer;
import com.hashjosh.farmer.mapper.UserMapper;
import com.hashjosh.farmer.repository.*;
import com.hashjosh.jwtshareable.service.JwtService;
import com.hashjosh.kafkacommon.farmer.FarmerRegistrationContract;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final FarmerProducer farmerProducer;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    @Transactional
    public Farmer register(RegistrationRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserException("Email already exists", HttpStatus.BAD_REQUEST.value());
        }

        if (userRepository.existsByUsername(request.getRsbsaId())) {
            throw new UserException("RSBSA ID already exists", HttpStatus.BAD_REQUEST.value());
        }

        Set<Role> roles = Collections.singleton(roleRepository.findByName("FARMER")
                .orElseThrow(() -> new UserException("Role not found", HttpStatus.NOT_FOUND.value())));


        // Create and save UserProfile first
        Farmer farmer = userMapper.toUserEntity(request,roles);

        // Save user (will cascade save the profile)
        Farmer registeredFarmer = userRepository.save(farmer);

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

    public LoginResponse login(LoginRequest request, String clientIp, String userAgent) {
        // ✅ authenticate user
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        Farmer farmer = userDetails.getFarmer();

        // Extract permissions first
        Set<String> permissions = farmer.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(Permission::getName)
                .collect(Collectors.toSet());

        // Convert roles to string names
        Set<String> roleNames = farmer.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        // Jwt claims for user
        Map<String, Object> claims = Map.of(
                "userId", farmer.getId(),
                "firstname", farmer.getFirstName(),
                "lastname", farmer.getLastName(),
                "email", farmer.getEmail(),
                "phoneNumber", farmer.getPhoneNumber(),
                "roles", roleNames,
                "permissions", permissions
        );

        // ✅ generate tokens (do NOT call login or authenticate again)
        String accessToken = jwtService.generateAccessToken(
                farmer.getUsername(),
                claims,
                jwtService.getAccessTokenExpiry(request.isRememberMe())
        );

        String refreshToken = jwtService.generateRefreshToken(
                farmer.getUsername(), clientIp, userAgent,
                jwtService.getRefreshTokenExpiry(request.isRememberMe())
        );

        return new LoginResponse(accessToken, refreshToken);
    }

    @Transactional(readOnly = true)
    public AuthenticatedResponse getAuthenticatedUser(Farmer request) {

        Farmer farmer = userRepository.findByIdWithRolesAndPermissions(request.getId())
                .orElseThrow(() -> new UserException("User not found", HttpStatus.NOT_FOUND.value()));

        return userMapper.toAuthenticatedResponse(farmer);
    }
}