package com.hashjosh.farmer.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
    @NotNull(message = "Username cannot be null")
    @Size(max = 50, message = "Username cannot exceed 50 characters")
    private String rsbsaId;
    @NotNull(message = "Password cannot be null")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;
    @NotNull(message = "First name cannot be null")
    private String firstName;
    @NotNull(message = "Last name cannot be null")
    private String lastName;
    @NotNull(message = "Middle name cannot be null")
    private String middleName;
    @NotNull(message = "Email cannot be null")
    private String email;
    @NotNull(message = "Phone number cannot be null")
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
