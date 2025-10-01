package com.hashjosh.pcic.service;


import com.hashjosh.jwtshareable.service.JwtService;
import com.hashjosh.kafkacommon.agriculture.AgricultureRegistrationContract;
import com.hashjosh.pcic.config.CustomUserDetails;
import com.hashjosh.pcic.dto.*;
import com.hashjosh.pcic.entity.*;
import com.hashjosh.pcic.exception.UserException;
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

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PcicProducer pcicProducer;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public User register(RegistrationRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserException("Email already exists", HttpStatus.BAD_REQUEST.value());
        }

        Set<Role> roles = new HashSet<>();
        request.getRolesId().forEach(roleId -> {
            Role role = roleRepository.findById(roleId)
                    .orElseThrow(() -> new UserException("Role not found", HttpStatus.NOT_FOUND.value()));
            roles.add(role);
        });

        User user = userMapper.toUserEntity(request, roles);
        user.setRoles(roles);

        User registeredUser = userRepository.save(user);

        publishUserRegistrationEvent(request,user);

        return registeredUser;
    }

    private void publishUserRegistrationEvent(RegistrationRequest request, User savedUser) {
        AgricultureRegistrationContract agricultureRegistrationContract =
                AgricultureRegistrationContract.builder()
                        .userId(savedUser.getId())
                        .username(savedUser.getUsername())
                        .password(request.getPassword())
                        .firstName(savedUser.getFirstName())
                        .lastName(savedUser.getLastName())
                        .email(savedUser.getEmail())
                        .phoneNumber(savedUser.getPhoneNumber())
                        .build();

        pcicProducer.publishPcicRegistrationEvent(agricultureRegistrationContract);
    }

    public LoginResponse login(LoginRequest request,String clientIp, String userAgent) {
        // ✅ authenticate user
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        User user = userDetails.getUser();

        // Jwt claims for user
        Map<String,Object> claims = Map.of(
                "userId", user.getId()
        );

        // ✅ generate tokens (do NOT call login or authenticate again)
        String accessToken = jwtService.generateAccessToken(
                user.getUsername(),
                claims,
                jwtService.getAccessTokenExpiry(request.isRememberMe())
        );

        String refreshToken = jwtService.generateRefreshToken(
                user.getUsername(), clientIp, userAgent,
                jwtService.getRefreshTokenExpiry(request.isRememberMe())
        );

        return new LoginResponse(accessToken, refreshToken);
    }

    @Transactional(readOnly = true)
    public AuthenticatedResponse getAuthenticatedUser(User request) {

        User user = userRepository.findByIdWithRolesAndPermissions(request.getId())
                .orElseThrow(() -> new UserException("User not found", HttpStatus.NOT_FOUND.value()));

        return userMapper.toAuthenticatedResponse(user);
    }
}
