package com.hashjosh.users.services;

import com.hashjosh.jwtshareable.service.JwtService;
import com.hashjosh.kafkacommon.user.FarmerRegistrationContract;
import com.hashjosh.kafkacommon.user.StaffRegistrationContract;
import com.hashjosh.kafkacommon.user.TenantType;
import com.hashjosh.users.clients.RsbsaResponseDto;
import com.hashjosh.users.clients.RsbsaServiceClient;
import com.hashjosh.users.config.CustomUserDetails;
import com.hashjosh.users.dto.AuthenticatedResponse;
import com.hashjosh.users.dto.LoginRequest;
import com.hashjosh.users.dto.LoginResponse;
import com.hashjosh.users.dto.RegistrationRequest;
import com.hashjosh.users.entity.Role;
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

        User user = userMapper.toStaffEntity(request, roles);
        user.setRoles(roles);

        User registeredUser = userRepository.save(user);
        // Publish the staff registration to user event topic  for notification
        RegistrationRequest.StaffCredentials credentials = new RegistrationRequest.StaffCredentials(
                user.getTenantType(),
                user.getId(),
                user.getUsername(),
                request.getPassword(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getAddress()
        );

        publishStaffRegistrationTopic(credentials);

        return registeredUser;

    }

    @Transactional
    public User registerFarmer(RegistrationRequest.FarmerRegistrationRequest farmerRequest) {
        // 1. Validate if email already exists
        if (userRepository.existsByEmail(farmerRequest.getEmail())) {
            throw new UserException("Email already exists", HttpStatus.BAD_REQUEST.value());
        }

        if(userRepository.existsByUsername(farmerRequest.getRsbsaNumber())){
            throw new UserException("Rsbsa number already have an associated account, please contact the customer if not yours", HttpStatus.BAD_REQUEST.value());
        }

        // Validate RSBSA Number if it exist!
        RsbsaResponseDto rsbsaInfo = rsbsaServiceClient.getRsbsa(farmerRequest.getRsbsaNumber());

        String generatedPassword = generateRandomPassword();
        // NOTE: Refactor this in the future make a username formatter function, becuase I know the real rsbsa number is 18 character
        // and usename must be 4-6 character only.
        String username = farmerRequest.getRsbsaNumber();

        Set<Role> farmerRoles = Collections.singleton(roleRepository.findByName(TenantType.FARMER.name().toUpperCase())
                .orElseThrow(() -> new UserException("Farmer role not found", HttpStatus.NOT_FOUND.value())));

        RegistrationRequest.FarmerRegistrationRequestWrapper farmerRequestWrapper = new RegistrationRequest.FarmerRegistrationRequestWrapper(
                username,
                generatedPassword,
                farmerRequest
        );

        User user = userMapper.toFarmerEntity(farmerRequestWrapper);
        user.setRoles(farmerRoles);

        User registeredUser = userRepository.save(user);

        RegistrationRequest.FarmerCredendials credendials = new RegistrationRequest.FarmerCredendials(
                registeredUser.getId(),
                farmerRequest.getRsbsaNumber(),
                farmerRequest.getRsbsaNumber(),
                generatedPassword,
                registeredUser.getFirstName(),
                registeredUser.getLastName(),
                farmerRequest.getMiddleName(),
                registeredUser.getEmail(),
                registeredUser.getPhoneNumber()
        );

        publishFarmerRegistrationTopic(credendials);

        return registeredUser;
    }

    private void publishFarmerRegistrationTopic(RegistrationRequest.FarmerCredendials credendials) {
        FarmerRegistrationContract contract = userMapper.toFarmerRegistrationContract(credendials);

        userRegistration.farmerRegistrationEvent(contract);
    }

    private void publishStaffRegistrationTopic(RegistrationRequest.StaffCredentials credentials) {
        StaffRegistrationContract contract = userMapper.toStaffRegistrationContract(credentials);

        userRegistration.staffRegistrationEvent(contract);
    }

    @Transactional(readOnly = true)
    public AuthenticatedResponse getAuthenticatedUser(User user) {

        User u = userRepository.findByIdWithRolesAndPermissions(user.getId())
                .orElseThrow(() -> new UserException("User not found", HttpStatus.NOT_FOUND.value()));

        return userMapper.toAuthenticatedResponse(u);
    }
    private String generateRandomPassword() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

}