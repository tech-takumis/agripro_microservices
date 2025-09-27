package com.hashjosh.users.services;

import com.hashjosh.jwtshareable.service.JwtService;
import com.hashjosh.kafkacommon.user.UserRegistrationContract;
import com.hashjosh.users.clients.RsbsaResponseDto;
import com.hashjosh.users.clients.RsbsaServiceClient;
import com.hashjosh.users.config.CustomUserDetails;
import com.hashjosh.users.dto.*;
import com.hashjosh.users.entity.Role;
import com.hashjosh.kafkacommon.user.TenantType;
import com.hashjosh.users.entity.User;
import com.hashjosh.users.exception.UserException;
import com.hashjosh.users.kafka.UserRegistrationProducer;
import com.hashjosh.users.mapper.UserMapper;
import com.hashjosh.users.repository.RoleRepository;
import com.hashjosh.users.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final UserServiceRefreshTokenStore refreshTokenStore;
    private final UserMapper userMapper;
    private final RoleRepository repository;
    private final RsbsaServiceClient rsbsaServiceClient;
    private final RoleRepository roleRepository;
    private final UserRegistrationProducer userRegistration;

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
    public User registerUser(RegistrationRequest.StaffRegistrationRequest request) {

        Set<Role> roles = new HashSet<>();
        request.getRolesId().forEach(roleId -> {
            Role role = repository.findById(roleId)
                    .orElseThrow(() -> new UserException("Role not found", HttpStatus.NOT_FOUND.value()));
            roles.add(role);
        });

        User user = userMapper.toEntity(request, roles);
        user.setRoles(roles);

        return userRepository.save(user);
    }

    @Transactional
    public User registerStaff(RegistrationRequest.StaffRegistrationRequest request) {
        // Check if
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserException("Email already exists", HttpStatus.BAD_REQUEST.value());
        }

        Set<Role> roles = new HashSet<>();
        request.getRolesId().forEach(roleId -> {
            Role role = repository.findById(roleId)
                    .orElseThrow(() -> new UserException("Role not found", HttpStatus.NOT_FOUND.value()));
            roles.add(role);
        });

        User user = userMapper.toEntity(request, roles);
        user.setRoles(roles);

        User registeredUser = userRepository.save(user);
        // Publish the staff registration to user event topic  for notification
        publishUserRegistrationTopic(user);

        return registeredUser;

    }

    @Transactional
    public User registerFarmer(RegistrationRequest.FarmerRegistrationRequest farmerRequest) {
        // 1. Validate if email already exists
        if (userRepository.existsByEmail(farmerRequest.getEmail())) {
            throw new UserException("Email already exists", HttpStatus.BAD_REQUEST.value());
        }

        RsbsaResponseDto rsbsaInfo = rsbsaServiceClient.getRsbsa(farmerRequest.getRsbsaNumber());

        RegistrationRequest.StaffRegistrationRequest userRequest = userMapper.toUserRequestEntity(farmerRequest);

        Set<Role> farmerRoles = Collections.singleton(roleRepository.findByName("FARMER")
                .orElseThrow(() -> new UserException("Farmer role not found", HttpStatus.NOT_FOUND.value())));

        userRequest.setRolesId(farmerRoles.stream().map(Role::getId).collect(Collectors.toSet()));

        User user = userMapper.toEntity(userRequest, farmerRoles);
        user.setRoles(farmerRoles);

        User registeredUser = userRepository.save(user);

        publishUserRegistrationTopic(registeredUser);

        return registeredUser;
    }

    private void publishUserRegistrationTopic(User registeredUser) {
        UserRegistrationContract contract = userMapper.toUserRegistrationContract(registeredUser);

        userRegistration.userRegistration(contract);
    }

    @Transactional(readOnly = true)
    public AuthenticatedResponse getAuthenticatedUser(User user) {

        User u = userRepository.findByIdWithRolesAndPermissions(user.getId())
                .orElseThrow(() -> new UserException("User not found", HttpStatus.NOT_FOUND.value()));

        return userMapper.toAuthenticatedResponse(u);
    }

}