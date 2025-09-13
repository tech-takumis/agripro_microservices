package com.hashjosh.users.services;

import com.hashjosh.jwtshareable.service.JwtService;
import com.hashjosh.users.config.CustomUserDetails;
import com.hashjosh.users.dto.LoginRequest;
import com.hashjosh.users.dto.LoginResponse;
import com.hashjosh.users.entity.User;
import com.hashjosh.users.entity.UserType;
import com.hashjosh.users.exception.UserException;
import com.hashjosh.users.repository.UserRepository;
import com.hashjosh.users.wrapper.UserRegistrationRequestWrapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final UserServiceRefreshTokenStore refreshTokenStore;

    @PostConstruct
    public void init() {
        // 🔗 Connect refresh token store to JwtService
        jwtService.setRefreshToken(refreshTokenStore);
    }

    public LoginResponse login(LoginRequest request, String tenantId, String clientIp, String userAgent) {
        // ✅ authenticate user
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        User user = userDetails.getUser();

        // ✅ generate tokens (do NOT call login or authenticate again)
        String accessToken = jwtService.generateAccessToken(
                user.getId(),
                user.getUsername(),
                tenantId,
                Map.of("role", user.getUserType().name()),
                jwtService.getAccessTokenExpiry(request.isRememberMe())
        );

        String refreshToken = jwtService.generateRefreshToken(
                user.getUsername(), clientIp, userAgent,
                jwtService.getRefreshTokenExpiry(request.isRememberMe())
        );

        return new LoginResponse(accessToken, refreshToken);
    }

    @Transactional
    public User registerUser(UserRegistrationRequestWrapper wrapper) {
        if (userRepository.existsByEmail(wrapper.request().getEmail())) {
            throw new UserException("Email already exists", HttpStatus.BAD_REQUEST.value());
        }

        User user = User.builder()
                .username(wrapper.request().getUsername())
                .password(passwordEncoder.encode(wrapper.request().getPassword()))
                .firstName(wrapper.request().getFirstName())
                .lastName(wrapper.request().getLastName())
                .email(wrapper.request().getEmail())
                .phoneNumber(wrapper.request().getPhoneNumber())
                .address(wrapper.request().getAddress())
                .userType(wrapper.userType())
                .build();

        return userRepository.save(user);
    }

    public List<User> getUsersByType(UserType userType) {
        return userRepository.findByUserType(userType);
    }


}