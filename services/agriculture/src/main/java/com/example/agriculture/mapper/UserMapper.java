package com.example.agriculture.mapper;

import com.example.agriculture.dto.auth.AuthenticatedResponse;
import com.example.agriculture.dto.rbac.PermissionResponse;
import com.example.agriculture.dto.auth.RegistrationRequest;
import com.example.agriculture.dto.rbac.RoleResponse;
import com.example.agriculture.entity.Agriculture;
import com.example.agriculture.entity.Role;
import com.example.agriculture.entity.AgricultureProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class UserMapper {

    private final PasswordEncoder passwordEncoder;

    public Agriculture toUserEntity(
            RegistrationRequest request, Set<Role> roles) {

        AgricultureProfile agricultureProfile = toUserProfileEntity(request);
        Agriculture agriculture =  Agriculture.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .agricultureProfile(agricultureProfile)
                .roles(roles)
                .build();

        agricultureProfile.setAgriculture(agriculture);

        return agriculture;
    }

    public AuthenticatedResponse toAuthenticatedResponse(Agriculture agriculture) {

        Set<RoleResponse> roles = new HashSet<>();

        if (agriculture.getRoles() != null) {
            for (Role role : agriculture.getRoles()) {
                RoleResponse roleResponse = toRoleResponse(role);
                roles.add(roleResponse);
            }
        }

        return new AuthenticatedResponse(
                agriculture.getId(),
                agriculture.getUsername(),
                agriculture.getFirstName(),
                agriculture.getLastName(),
                agriculture.getEmail(),
                agriculture.getPhoneNumber(),
                roles
        );
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

    public RoleResponse toRoleResponse(Role role) {
        return RoleResponse.builder()
                .id(role.getId())
                .name(role.getName())
                .slug(role.getSlug())
                .permissions(role.getPermissions() != null ? role.getPermissions().stream().map(permission ->
                        new PermissionResponse(
                                permission.getId(),
                                permission.getName(),
                                permission.getSlug(),
                                permission.getDescription()
                        )).toList() : null)
                .defaultRoute(role.getDefaultRoute())
                .build();
    }
}
