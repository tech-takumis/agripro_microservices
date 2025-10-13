package com.example.agriculture.service;

import com.example.agriculture.config.CustomUserDetails;
import com.example.agriculture.dto.auth.LoginRequest;
import com.example.agriculture.dto.auth.LoginResponse;
import com.example.agriculture.dto.auth.RegistrationRequest;
import com.example.agriculture.dto.rbac.RoleResponse;
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
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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

        agricultureProducer.publishEvent("agriculture-events",agricultureRegistrationContract);
    }

    public LoginResponse login(LoginRequest request, HttpServletRequest httpRequest) {
        // ✅ authenticate user
        String clientIp = httpRequest.getRemoteAddr();
        String userAgent = httpRequest.getHeader(HttpHeaders.USER_AGENT);

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        // Set authentication in security context
        SecurityContextHolder.getContext().setAuthentication(auth);


        // Get the CustomUserDetails from the authenticated principal
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();

        // Build JWT claims from the authenticated user
        Map<String, Object> claims = buildClaims(getAgricultureWithRoles(userDetails.getUserId()));

        // ✅ generate tokens
        String accessToken = jwtService.generateAccessToken(
                userDetails.getUsername(),
                claims,
                jwtService.getAccessTokenExpiry(request.isRememberMe())
        );

        String refreshToken = jwtService.generateRefreshToken(
                userDetails.getUsername(), clientIp, userAgent,
                jwtService.getRefreshTokenExpiry(request.isRememberMe())
        );

        return new LoginResponse(accessToken, refreshToken);
    }

    public Agriculture getAgricultureWithRoles(UUID userId) {
        return agricultureRepository.findByIdWithRolesAndPermissions(userId)
                .orElseThrow(() -> new UserException("User not found", HttpStatus.NOT_FOUND.value()));
    }
    public RoleResponse getRole(Role role) {
        return userMapper.toRoleResponse(role);
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

    private Map<String,Object> buildClaims(Agriculture agriculture) {

        // Extract permissions first
        Set<String> permissions = extractPermissions(agriculture.getRoles());

        // Convert roles to string names
        Set<String> roles = extractRoles(agriculture.getRoles());

        return Map.of(
                "userId", agriculture.getId(),
                "firstname", agriculture.getFirstName(),
                "lastname", agriculture.getLastName(),
                "email", agriculture.getEmail(),
                "phoneNumber", agriculture.getPhoneNumber(),
                "roles", roles,
                "permissions", permissions
        );
    }
}