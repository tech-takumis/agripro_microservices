package com.example.agriculture.mapper;

import com.example.agriculture.dto.auth.AgricultureResponseDto;
import com.example.agriculture.dto.auth.RegistrationRequest;
import com.example.agriculture.dto.rbac.RoleResponse;
import com.example.agriculture.entity.Agriculture;
import com.example.agriculture.entity.AgricultureProfile;
import com.example.agriculture.entity.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserMapper {

    private final PasswordEncoder passwordEncoder;
    private final RoleMapper roleMapper;

    public Agriculture toUserEntity(
            RegistrationRequest request, Set<Role> roles,
            String username) {


        AgricultureProfile agricultureProfile = toUserProfileEntity(request);
        Agriculture agriculture =  Agriculture.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .username(username)
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .agricultureProfile(agricultureProfile)
                .roles(roles)
                .build();

        agricultureProfile.setAgriculture(agriculture);

        return agriculture;
    }

    public AgricultureResponseDto toAgricultureResponseDto(Agriculture agriculture){

        Set<RoleResponse> roles = agriculture.getRoles().stream()
                .map(roleMapper::toRoleResponse)
                .collect(Collectors.toSet());

        return AgricultureResponseDto.builder()
                .id(agriculture.getId())
                .username(agriculture.getUsername())
                .firstName(agriculture.getFirstName())
                .address(formatAddress(agriculture.getAgricultureProfile()))
                .lastName(agriculture.getLastName())
                .email(agriculture.getEmail())
                .phoneNumber(agriculture.getPhoneNumber())
                .roles(roles)
                .build();
    }


    public AgricultureProfile toUserProfileEntity(RegistrationRequest request) {
        return AgricultureProfile.builder()
                .street(request.getStreet())
                .barangay(request.getBarangay())
                .city(request.getCity())
                .province(request.getProvince())
                .country(request.getCountry())
                .region(request.getRegion())
                .postalCode(request.getPostalCode())
                .publicAffairsEmail(request.getPublicAffairsEmail())
                .headquartersAddress(request.getHeadquartersAddress())
                .build();
    }

    private String formatAddress(AgricultureProfile profile) {
        return String.join(", ",
                profile.getStreet(),
                profile.getBarangay(),
                profile.getCity(),
                profile.getProvince(),
                profile.getRegion(),
                profile.getCountry(),
                profile.getPostalCode()
        );
    }
}
