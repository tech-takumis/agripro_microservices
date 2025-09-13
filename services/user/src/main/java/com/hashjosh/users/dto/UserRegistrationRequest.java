package com.hashjosh.users.dto;

import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Data
public class UserRegistrationRequest {
    private String username;
    private String firstName;
    private String lastName;
    private String password;
    private String email;
    private String phoneNumber;
    private String address;
    private Set<UUID> rolesId;
}

