package com.hashjosh.users.services;

import com.hashjosh.jwtshareable.service.JwtService;
import com.hashjosh.users.config.CustomUserDetails;
import com.hashjosh.users.dto.AuthenticatedResponse;
import com.hashjosh.users.dto.LoginRequest;
import com.hashjosh.users.dto.LoginResponse;
import com.hashjosh.users.dto.UserResponse;
import com.hashjosh.users.entity.Role;
import com.hashjosh.users.entity.TenantType;
import com.hashjosh.users.entity.User;
import com.hashjosh.users.exception.UserException;
import com.hashjosh.users.mapper.UserMapper;
import com.hashjosh.users.repository.RoleRepository;
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

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final UserServiceRefreshTokenStore refreshTokenStore;
    private final UserMapper userMapper;
    private final RoleRepository repository;

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

        // Jwt claims for user
        Map<String,Object> claims = Map.of(
                "userId", user.getId(),
                "tenantId", tenantId
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

    @Transactional
    public User registerUser(UserRegistrationRequestWrapper wrapper) {
        if (userRepository.existsByEmail(wrapper.request().getEmail())) {
            throw new UserException("Email already exists", HttpStatus.BAD_REQUEST.value());
        }

        Set<Role> roles = new HashSet<>();

        wrapper.request().getRolesId().forEach(roleId -> {
            Role role = repository.findById(roleId).orElseThrow(() -> new UserException("Role not found", HttpStatus.NOT_FOUND.value()));
            roles.add(role);
        });

        User user = userMapper.toEntity(wrapper, roles);
        user.setRoles(roles);

        return userRepository.save(user);
    }

    public List<User> getUsersByType(TenantType tenantType) {
        return userRepository.findByTenantType(tenantType);
    }


    @Transactional(readOnly = true)
    public AuthenticatedResponse getAuthenticatedUser(User user) {

        User u = userRepository.findByIdWithRolesAndPermissions(user.getId())
                .orElseThrow(() -> new UserException("User not found", HttpStatus.NOT_FOUND.value()));

        // Force initialization of lazy collections
        u.getRoles().forEach(role -> role.getPermissions().size());

        return userMapper.toAuthenticatedResponse(u);
    }

    public UserResponse getUserById(UUID userId) {
        User user =  userRepository.findById(userId)
                .orElseThrow(() -> new UserException(
                        "User id "+userId+ " not found!",
                        HttpStatus.NOT_FOUND.value()
                ));

        return userMapper.toUserResponse(user);
    }
}