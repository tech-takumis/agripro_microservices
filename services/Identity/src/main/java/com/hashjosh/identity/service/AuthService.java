package com.hashjosh.identity.service;

import com.hashjosh.constant.user.UserResponseDTO;
import com.hashjosh.identity.config.CustomUserDetails;
import com.hashjosh.identity.dto.LoginRequest;
import com.hashjosh.identity.dto.LoginResponse;
import com.hashjosh.identity.dto.UserRegistrationRequest;
import com.hashjosh.identity.entity.*;
import com.hashjosh.identity.exception.ApiException;
import com.hashjosh.identity.kafka.KafkaPublisher;
import com.hashjosh.identity.mapper.UserMapper;
import com.hashjosh.identity.properties.JwtProperties;
import com.hashjosh.identity.repository.*;
import com.hashjosh.kafkacommon.user.RequestPasswordResetEvent;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
    private final TenantProfileFieldRepository tenantProfileFieldRepository;
    private final UserAttributeRepository userAttributeRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtProperties  jwtProperties;
    private final JwtService jwtService;
    private final KafkaPublisher kafkaPublisher;

    @Transactional
    public User registerUser(UserRegistrationRequest request) {
        // 1️⃣ Find Tenant by Code
        Tenant tenant = getOrCreateTenant(request.getTenantKey());
        String passwordHash = passwordEncoder.encode(request.getPassword());


        Set<Role> userRoles = new HashSet<>();
        // Get the role of the user using tenant per role
        // So we check role requested by user belongs to the tenant
        Role role = roleRepository.findByTenantKeyIgnoreCaseAndNameIgnoreCase(request.getTenantKey(), request.getRoleName())
                .orElseThrow(() -> ApiException.badRequest("Role not found: " + request.getRoleName() + " for tenant: " + request.getTenantKey()));

        userRoles.add(role);
        // 3️⃣ Create User
        User user = userMapper.toUserEntity(request, tenant, passwordHash);
        user.setRoles(userRoles);
        userRepository.save(user);

        // 4️⃣ Get tenant’s field definitions
        List<TenantProfileField> tenantFields =
                tenantProfileFieldRepository.findByTenantId(tenant.getId());

        // 5️⃣ Validate + store attributes
        if (request.getProfile() != null) {
            for (Map.Entry<String, Object> entry : request.getProfile().entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();

                // ensure key exists in tenant’s schema
                Optional<TenantProfileField> fieldDef = tenantFields.stream()
                        .filter(f -> f.getFieldKey().equalsIgnoreCase(key))
                        .findFirst();

                if (fieldDef.isEmpty()) {
                    throw ApiException.badRequest("Invalid profile field for tenant: " + key);
                }

                UserAttribute attr = UserAttribute.builder()
                        .user(user)
                        .fieldKey(key)
                        .fieldValue(value != null ? value.toString() : null)
                        .build();
                userAttributeRepository.save(attr);
            }
        }

        return user;
    }

    private Tenant getOrCreateTenant(String tenantCode) {
        return tenantRepository.findByKeyIgnoreCase(tenantCode)
                .orElseThrow(() -> ApiException.notFound("Tenant not found with key: " + tenantCode));
    }

    @Transactional
    public LoginResponse login(LoginRequest request, HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> ApiException.notFound("User not found with username: " + request.getUsername()));

        if(!userRepository.existsByTenantKeyIgnoreCaseAndId(request.getTenantKey(),user.getId())){
            throw ApiException.badRequest("Invalid tenant key for user: " + request.getUsername());
        }
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

        User user = userRepository.findById(userDetails.getUserId())
                .orElseThrow(() -> ApiException.notFound("User not found with ID: " + userDetails.getUserId()));

        return userMapper.toUserResponseDto(user);
    }
}
