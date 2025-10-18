package com.hashjosh.farmer.dto;

import com.hashjosh.farmer.entity.FarmerProfile;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthUserResponse {
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private Set<RoleResponse> roles;
    private FarmerProfile profile;
}
