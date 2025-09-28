package com.hashjosh.users.mapper;

import com.hashjosh.kafkacommon.user.FarmerRegistrationContract;
import com.hashjosh.kafkacommon.user.StaffRegistrationContract;
import com.hashjosh.users.dto.AuthenticatedResponse;
import com.hashjosh.users.dto.RegistrationRequest;
import com.hashjosh.users.dto.UserResponse;
import com.hashjosh.users.dto.permission.PermissionResponse;
import com.hashjosh.users.dto.role.RoleResponse;
import com.hashjosh.users.entity.Role;
import com.hashjosh.users.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserMapper {

    private final PasswordEncoder passwordEncoder;
    private final RoleMapper roleMapper;
    private final PermissionMapper permissionMapper;

    public AuthenticatedResponse toAuthenticatedResponse(User user) {
        Set<String> roles = new HashSet<>();
        Set<String> permissions = new HashSet<>();

        // roles and permissions are now initialized
        user.getRoles().forEach(role -> {
            roles.add(role.getName());
            role.getPermissions().forEach(permission -> permissions.add(permission.getName()));
        });


        return new AuthenticatedResponse(
            user.getId(),
                user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getAddress(),
                roles,
                permissions
        );
    }

    public User toStaffEntity(
            RegistrationRequest.StaffRegistrationRequest request, Set<Role> roles) {
        return User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .address(request.getAddress())
                .tenantType(request.getTenantId())
                .roles(roles)
                .build();
    }

    public UserResponse toUserResponse(User user) {

        // Get roles and permissions for user
        Set<RoleResponse> roles = new HashSet<>();

        user.getRoles().forEach(role -> {
            // Get the permissions for the role
            RoleResponse roleResponse = roleMapper.toRoleResponse(role);
            List<PermissionResponse> permissionResponses = new ArrayList<>();
            role.getPermissions().forEach(permission -> {
                permissionResponses.add(roleMapper.toPermissionResponse(permission));
            });
            roleResponse.setPermissions(permissionResponses);
            roles.add(roleResponse);
        });

        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .address(user.getAddress())
                .roles(roles)
                .build();
    }

    public RegistrationRequest.StaffRegistrationRequest toUserRequestEntity(
            RegistrationRequest.FarmerRegistrationRequest farmerRequest,
            String username, String generatedPassword) {
        String address = String.format("%s, %s, %s, %s",
                farmerRequest.getCity(),
                farmerRequest.getState(),
                farmerRequest.getZipCode(),
                farmerRequest.getCountry());

        return RegistrationRequest.StaffRegistrationRequest.builder()
                .tenantId(farmerRequest.getTenantId())
                .firstName(farmerRequest.getFirstName())
                .lastName(farmerRequest.getLastName())
                .email(farmerRequest.getEmail())
                .phoneNumber(farmerRequest.getPhoneNumber())
                .password(passwordEncoder.encode(generatedPassword))
                .username(username)
                .address(address)
                .build();
    }



    public FarmerRegistrationContract toFarmerRegistrationContract(RegistrationRequest.FarmerCredendials credendials) {
        return FarmerRegistrationContract.builder()
                .userId(credendials.getUserId())
                .rsbsaId(credendials.getRsbsaNumber())
                .username(credendials.getUsername())
                .password(credendials.getPassword())
                .firstName(credendials.getFirstName())
                .lastName(credendials.getLastName())
                .middleName(credendials.getMiddleName())
                .email(credendials.getEmail())
                .phoneNumber(credendials.getPhoneNumber())
                .build();

    }

    public StaffRegistrationContract toStaffRegistrationContract(RegistrationRequest.StaffCredentials credentials) {
        return StaffRegistrationContract.builder()
                .userId(credentials.getUserId())
                .username(credentials.getUsername())
                .password(credentials.getPassword())
                .firstName(credentials.getFirstName())
                .lastName(credentials.getLastName())
                .email(credentials.getEmail())
                .phoneNumber(credentials.getPhoneNumber())
                .build();
    }

    public User toFarmerEntity(RegistrationRequest.FarmerRegistrationRequestWrapper farmerRequestWrapper) {
        String address = String.format("%s, %s, %s, %s",
                farmerRequestWrapper.getFarmerRegistrationRequest().getCity(),
                farmerRequestWrapper.getFarmerRegistrationRequest().getState(),
                farmerRequestWrapper.getFarmerRegistrationRequest().getZipCode(),
                farmerRequestWrapper.getFarmerRegistrationRequest().getCountry());
        return User.builder()
                .username(farmerRequestWrapper.getUsername())
                .password(passwordEncoder.encode(farmerRequestWrapper.getPassword()))
                .firstName(farmerRequestWrapper.getFarmerRegistrationRequest().getFirstName())
                .lastName(farmerRequestWrapper.getFarmerRegistrationRequest().getLastName())
                .email(farmerRequestWrapper.getFarmerRegistrationRequest().getEmail())
                .phoneNumber(farmerRequestWrapper.getFarmerRegistrationRequest().getPhoneNumber())
                .address(address)
                .tenantType(farmerRequestWrapper.getFarmerRegistrationRequest().getTenantId())
                .build();
    }
}
