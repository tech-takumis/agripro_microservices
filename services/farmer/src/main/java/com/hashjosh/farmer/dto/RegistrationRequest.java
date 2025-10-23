package com.hashjosh.farmer.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Builder
public class RegistrationRequest {
    // User
    private String rsbsaId;
    private String password;
    private String firstName;
    private String lastName;
    private String middleName;
    private String email;
    private String phoneNumber;

    // User Profile
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;
    private String gender;
    private String civilStatus;

    private String street;
    private String barangay;
    private String municipality;
    private String province;
    private String region;

    private String farmerType;
    private Double totalFarmAreaHa;
}
