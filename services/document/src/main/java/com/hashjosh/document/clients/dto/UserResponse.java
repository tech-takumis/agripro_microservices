package com.hashjosh.document.config;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Data
@Builder
public class UserResponse {
    private UUID id;
    private String username;
    private String firstName;
    private String lastName;
    private String password;
    private String email;
    private String phoneNumber;
    private String address;
    private Set<RoleResponse> roles;
}
