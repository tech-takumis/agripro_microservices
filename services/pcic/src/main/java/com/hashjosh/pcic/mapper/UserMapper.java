package com.hashjosh.pcic.mapper;


import com.hashjosh.pcic.dto.*;
import com.hashjosh.pcic.entity.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class UserMapper {

    private final PasswordEncoder passwordEncoder;

    public PcicProfile toPcicProfileEntity(RegistrationRequest request) {
        return PcicProfile.builder()
                .mandate(request.getMandate())
                .mission(request.getMission())
                .vision(request.getVision())
                .coreValues(request.getCoreValues())
                .headOfficeAddress(request.getHeadOfficeAddress())
                .phone(request.getPhone())
                .pcicEmail(request.getPcicEmail())
                .website(request.getWebsite())
                .build();
    }
    public Pcic toUserEntity(
            RegistrationRequest request, Set<Role> roles) {

        PcicProfile profile = toPcicProfileEntity(request);
        return Pcic.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .address(request.getAddress())
                .roles(roles)
                .pcicProfile(profile)
                .build();
    }

    public AuthenticatedResponse toAuthenticatedResponse(Pcic pcic) {
        Set<String> roles = new HashSet<>();
        Set<String> permissions = new HashSet<>();

        // roles and permissions are now initialized
        pcic.getRoles().forEach(role -> {
            roles.add(role.getName());
            role.getPermissions().forEach(permission -> permissions.add(permission.getName()));
        });


        return new AuthenticatedResponse(
                pcic.getId(),
                pcic.getUsername(),
                pcic.getFirstName(),
                pcic.getLastName(),
                pcic.getEmail(),
                pcic.getPhoneNumber(),
                pcic.getAddress(),
                roles,
                permissions
        );
    }
}
