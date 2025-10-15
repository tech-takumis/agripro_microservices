package com.hashjosh.farmer.dto;

import com.hashjosh.farmer.entity.FarmerProfile;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Builder
public class AuthenticatedResponse {
    private UUID id;
    private String accessToken;
    private String refreshToken;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private Set<RoleResponse> roles;
    private FarmerProfile profile;
}
