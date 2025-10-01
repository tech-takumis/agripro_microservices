package com.hashjosh.farmer.mapper;

import com.hashjosh.farmer.dto.AuthenticatedResponse;
import com.hashjosh.farmer.dto.RegistrationRequest;
import com.hashjosh.farmer.entity.Role;
import com.hashjosh.farmer.entity.User;
import com.hashjosh.farmer.entity.UserProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class UserMapper {

    private final PasswordEncoder passwordEncoder;


    public UserProfile toUserProfileEntity(RegistrationRequest request) {
        return UserProfile.builder()
                .rsbsaId(request.getRsbsaId())
                .dateOfBirth(request.getDateOfBirth())
                .gender(request.getGender())
                .civilStatus(request.getCivilStatus())
                .houseNo(request.getHouseNo())
                .street(request.getStreet())
                .barangay(request.getBarangay())
                .municipality(request.getMunicipality())
                .province(request.getProvince())
                .region(request.getRegion())
                .farmerType(request.getFarmerType())
                .totalFarmAreaHa(request.getTotalFarmAreaHa())
                .build();
    }
    public User toUserEntity(RegistrationRequest request, Set<Role> roles) {
        // Create UserProfile
        UserProfile userProfile = toUserProfileEntity(request);

        // Create User with the profile
        User user = User.builder()
                .username(request.getRsbsaId())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .roles(roles)
                .userProfile(userProfile)
                .build();

        // Set bidirectional relationship
        userProfile.setUser(user);

        return user;
    }

    public AuthenticatedResponse toAuthenticatedResponse(User user) {
        Set<String> roles = new HashSet<>();
        Set<String> permissions = new HashSet<>();

        user.getRoles().forEach(role -> {
            roles.add(role.getName());
            role.getPermissions().forEach(permission -> 
                permissions.add(permission.getName()));
        });

        return AuthenticatedResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .roles(roles)
                .permissions(permissions)
                .profile(user.getUserProfile())
                .build();
    }
}