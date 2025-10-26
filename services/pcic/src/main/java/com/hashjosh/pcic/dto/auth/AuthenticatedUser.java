package com.hashjosh.pcic.dto.auth;

import com.hashjosh.pcic.dto.role.RoleResponse;
import lombok.*;

import java.util.Set;
import java.util.UUID;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthenticatedUser {
    private UUID userId;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String address;
    private Set<RoleResponse> roles;
}
