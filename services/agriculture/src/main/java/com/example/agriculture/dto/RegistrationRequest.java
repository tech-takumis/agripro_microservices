package com.example.agriculture.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Builder
public class RegistrationRequest {
    // User
    private String username;
    private String firstName;
    private String lastName;
    private String password;
    private String email;
    private String phoneNumber;
    private String address;
    private Set<UUID> rolesId;

    // Profile
    private String headquartersAddress;
    private String publicAffairsEmail;

    // Address
    private String street;
    private String barangay;
    private String city;
    private String province;
    private String region;
    private String country;
    private String postalCode;

}
