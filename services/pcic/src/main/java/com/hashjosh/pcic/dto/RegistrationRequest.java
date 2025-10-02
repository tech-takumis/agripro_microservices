package com.hashjosh.pcic.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Builder
public class RegistrationRequest {
    private String username;
    private String firstName;
    private String lastName;
    private String password;
    private String email;
    private String phoneNumber;
    private String address;
    private Set<UUID> rolesId;
}
