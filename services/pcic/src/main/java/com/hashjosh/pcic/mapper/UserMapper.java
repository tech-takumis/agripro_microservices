package com.hashjosh.pcic.mapper;


import com.hashjosh.pcic.dto.auth.AuthenticatedResponse;
import com.hashjosh.pcic.dto.auth.AuthenticatedUser;
import com.hashjosh.pcic.dto.auth.LoginResponse;
import com.hashjosh.pcic.dto.auth.RegistrationRequest;
import com.hashjosh.pcic.dto.role.RoleResponse;
import com.hashjosh.pcic.entity.*;
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
        Pcic pcic =  Pcic.builder()
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

        profile.setPcic(pcic);
        return pcic;
    }

    public AuthenticatedUser toAuthenticatedResponse(Pcic pcic) {
        Set<RoleResponse> roles = pcic.getRoles().stream()
                .map(roleMapper::toRoleResponse)
                .collect(Collectors.toSet());

        return AuthenticatedUser.builder()
                .userId(pcic.getId())
                .username(pcic.getUsername())
                .firstName(pcic.getFirstName())
                .lastName(pcic.getLastName())
                .email(pcic.getEmail())
                .phoneNumber(pcic.getPhoneNumber())
                .address(pcic.getAddress())
                .roles(roles)
                .build();
    }

}
// No changes needed for circular reference.
