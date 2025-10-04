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

    // Profile section
    private String mandate;
    private String vision;
    private String mission;
    private String coreValues;
    private String headOfficeAddress;
    private String phone;
    private String pcicEmail;
    private String website;
}
