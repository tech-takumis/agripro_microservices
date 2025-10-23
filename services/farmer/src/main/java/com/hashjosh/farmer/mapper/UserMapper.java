package com.hashjosh.farmer.mapper;

import com.hashjosh.farmer.dto.AuthUserResponse;
import com.hashjosh.farmer.dto.AuthenticatedResponse;
import com.hashjosh.farmer.dto.RegistrationRequest;
import com.hashjosh.farmer.dto.RoleResponse;
import com.hashjosh.farmer.entity.Role;
import com.hashjosh.farmer.entity.Farmer;
import com.hashjosh.farmer.entity.FarmerProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserMapper {

    private final PasswordEncoder passwordEncoder;
    private final RoleMapper roleMapper;


    public FarmerProfile toUserProfileEntity(RegistrationRequest request) {
        return FarmerProfile.builder()
                .rsbsaId(request.getRsbsaId())
                .dateOfBirth(request.getDateOfBirth())
                .gender(request.getGender())
                .civilStatus(request.getCivilStatus())
                .street(request.getStreet())
                .barangay(request.getBarangay())
                .municipality(request.getMunicipality())
                .province(request.getProvince())
                .region(request.getRegion())
                .farmerType(request.getFarmerType())
                .totalFarmAreaHa(request.getTotalFarmAreaHa())
                .build();
    }
    public Farmer toUserEntity(RegistrationRequest request, Set<Role> roles) {
        // Create UserProfile
        FarmerProfile farmerProfile = toUserProfileEntity(request);

        // Create User with the profile
        Farmer farmer = Farmer.builder()
                .username(request.getRsbsaId())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .roles(roles)
                .farmerProfile(farmerProfile)
                .build();

        // Set bidirectional relationship
        farmerProfile.setFarmer(farmer);

        return farmer;
    }

    public AuthenticatedResponse toAuthenticatedResponse(Farmer farmer,String accessToken, String refreshToken, String websocketToken) {

        return AuthenticatedResponse.builder()
                .id(farmer.getId())
                .websocketToken(websocketToken)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(toAuthUserResponse(farmer))
                .build();
    }

    public AuthUserResponse toAuthUserResponse(Farmer farmer) {

        Set<RoleResponse> roles = farmer.getRoles().stream()
                .map(roleMapper::toRoleResponse)
                .collect(Collectors.toSet());

        return AuthUserResponse.builder()
                .username(farmer.getUsername())
                .firstName(farmer.getFirstName())
                .lastName(farmer.getLastName())
                .email(farmer.getEmail())
                .phoneNumber(farmer.getPhoneNumber())
                .roles(roles)
                .profile(farmer.getFarmerProfile())
                .build();
    }
}