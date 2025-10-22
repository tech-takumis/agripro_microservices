package com.hashjosh.identity.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hashjosh.constant.user.UserResponseDTO;
import com.hashjosh.identity.config.CustomUserDetails;
import com.hashjosh.identity.dto.LoginRequest;
import com.hashjosh.identity.dto.LoginResponse;
import com.hashjosh.identity.dto.RegistrationRequest;
import com.hashjosh.identity.entity.*;
import com.hashjosh.identity.exception.ApiException;
import com.hashjosh.identity.kafka.KafkaPublisher;
import com.hashjosh.identity.mapper.UserMapper;
import com.hashjosh.identity.properties.JwtProperties;
import com.hashjosh.identity.repository.*;
import com.hashjosh.identity.validator.UserRegistrationValidator;
import com.hashjosh.kafkacommon.user.RequestPasswordResetEvent;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final RoleRepository roleRepository;
    private final ObjectMapper objectMapper;
    private final UserRegistrationValidator registrationValidator;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtProperties  jwtProperties;
    private final JwtService jwtService;
    private final KafkaPublisher kafkaPublisher;


    @Transactional
    public LoginResponse login(LoginRequest request, HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> ApiException.notFound("User not found with username: " + request.getUsername()));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword()))
            throw ApiException.badRequest("Invalid username or password");


        // Determine expiration based on rememberMe
        boolean rememberMe = request.getRememberMe();
        long accessTokenExpiry = rememberMe
                ? jwtProperties.getAccessTokenExpirationRememberMeMs()
                : jwtProperties.getAccessTokenExpirationMs();
        long refreshTokenExpiry = rememberMe
                ? jwtProperties.getRefreshTokenExpirationRememberMeMs()
                : jwtProperties.getRefreshTokenExpirationMs();

        // Generate tokens
        String accessToken = jwtService.generateAuthToken(user, accessTokenExpiry);
        String refreshTokenValue = jwtService.generateSecureRefreshToken();
        String websocketToken = jwtService.generateAuthToken(user, jwtProperties.getWebSocketExpirationMs());

        String userAgent = Optional.ofNullable(httpRequest.getHeader("User-Agent")).orElse("unknown");

        // Save refresh token
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(refreshTokenValue)
                .userAgent(userAgent)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusSeconds(refreshTokenExpiry / 1000))
                .revoked(false)
                .build();
        refreshTokenRepository.save(refreshToken);

        // Detect browser-based client
        boolean isBrowser = userAgent.toLowerCase().contains("chrome")
                || userAgent.toLowerCase().contains("mozilla");

        if (isBrowser) {
            Cookie accessCookie = new Cookie("ACCESS_TOKEN", accessToken);
            accessCookie.setHttpOnly(true);
            accessCookie.setPath("/");
            accessCookie.setMaxAge((int) (accessTokenExpiry / 1000)); // seconds

            Cookie refreshCookie = new Cookie("REFRESH_TOKEN", refreshTokenValue);
            refreshCookie.setHttpOnly(true);
            refreshCookie.setPath("/");
            refreshCookie.setMaxAge((int) (refreshTokenExpiry / 1000)); // seconds

            httpResponse.addCookie(accessCookie);
            httpResponse.addCookie(refreshCookie);

            return LoginResponse.builder()
                    .accessToken(null) // don’t expose tokens for browsers
                    .refreshToken(null)
                    .websocketToken(websocketToken)
                    .user(userMapper.toUserResponseDto(user))
                    .build();
        } else {
            // For non-browser (e.g., mobile or API clients)
            return LoginResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshTokenValue)
                    .websocketToken(websocketToken)
                    .user(userMapper.toUserResponseDto(user))
                    .build();
        }
    }


    @Transactional
    public void logout(HttpServletRequest request, HttpServletResponse response, UUID userId) {
        // Revoke all user's refresh tokens
        List<RefreshToken> tokens = refreshTokenRepository.findAllByUser(
                userRepository.findById(userId).orElseThrow(() -> ApiException.notFound("User not found with ID: " + userId))
        );
        tokens.forEach(t -> t.setRevoked(true));
        refreshTokenRepository.saveAll(tokens);

        // Remove cookies
        Cookie accessCookie = new Cookie("ACCESS_TOKEN", null);
        accessCookie.setPath("/");
        accessCookie.setMaxAge(0);

        Cookie refreshCookie = new Cookie("REFRESH_TOKEN", null);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(0);

        response.addCookie(accessCookie);
        response.addCookie(refreshCookie);
    }

    @Transactional
    public void requestPasswordReset(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> ApiException.notFound("User not found with email: " + email));

        String token = UUID.randomUUID().toString();

        PasswordResetToken resetToken = PasswordResetToken.builder()
                .user(user)
                .token(token)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(30))
                .used(false)
                .build();

        passwordResetTokenRepository.save(resetToken);

        RequestPasswordResetEvent event = RequestPasswordResetEvent.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .token(token)
                .build();

        kafkaPublisher.publishEvent("user-lifecycle",event);
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> ApiException.notFound("Password reset token not found with token: " + token));

        if (resetToken.isUsed() || resetToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw ApiException.conflict("Reset token expired or already used");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        resetToken.setUsed(true);
        passwordResetTokenRepository.save(resetToken);
    }

    public UserResponseDTO getAuthenticatedUser() {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();

        User user = userRepository.findById(userDetails.getUser().getId())
                .orElseThrow(() -> ApiException.notFound("User not found with ID: " + userDetails.getUser()));

        return userMapper.toUserResponseDto(user);
    }

    @Transactional
    public void register(RegistrationRequest request) {
        log.debug("Processing registration request for tenant: {}", request.getTenantKey());

        // Validate registration request
        registrationValidator.validate(request);

        // Get or validate tenant
        Tenant tenant = getTenantForRegistration(request.getTenantKey());

        // Get roles for the user
        Set<Role> roles = getRolesForRegistration(tenant, request.getRoles());

        // Create and save user
        User user = createUser(request, tenant, roles);

    }

    private Tenant getTenantForRegistration(String tenantKey) {
        return tenantRepository.findByKeyIgnoreCase(tenantKey)
                .orElseThrow(() -> ApiException.notFound("Tenant not found: " + tenantKey));
    }

    private Set<Role> getRolesForRegistration(Tenant tenant, List<String> roleNames) {
        Set<Role> roles = new HashSet<>();
        for (String roleName : roleNames) {
            Role role = roleRepository.findByTenantAndNameContainingIgnoreCase(tenant, roleName)
                    .orElseThrow(() -> ApiException.notFound(
                            String.format("Role '%s' not found for tenant: %s", roleName, tenant.getName())
                    ));
            roles.add(role);
        }
        return roles;
    }

    private User createUser(RegistrationRequest request, Tenant tenant, Set<Role> roles) {
        // Check if username or email already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw ApiException.conflict("Username already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw ApiException.conflict("Email already exists");
        }

        User user = User.builder()
                .username(request.getUsername())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .tenant(tenant)
                .roles(roles)
                .active(true)
                .emailVerified(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .profileData(objectMapper.valueToTree(request.getProfile()))
                .build();

        return userRepository.save(user);
    }

}
