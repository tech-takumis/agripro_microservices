package com.example.agriculture.dto.auth;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Builder
public class RegistrationRequest {

    @NotBlank(message = "Firstname is required")
    @Size(max = 100,message = "Firstname cannot be exceed 100 character")
    private String firstName;

    @NotBlank(message = "Lastname is required")
    @Size(max = 100,message = "Lastname cannot be exceed 100 character")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(
            regexp = "^\\+63\\d{10}$",
            message = "Phone number must start with +63 followed by 10 digits (e.g. +639123456789)"
    )
    private String phoneNumber;
    @NotBlank(message = "Address is required")
    @Size(max = 100,message = "Address cannot be exceed 100 character")
    private String address;

    @NotEmpty(message = "Agriculture must have at least one role associated to it")
    private Set<String> roleNames;

    // Profile
    @NotBlank(message = "Head quarter address is required")
    @Size(max = 100, message = "Head quarter addess must not exceed up to 100 character only")
    private String headquartersAddress;

    @NotBlank(message = "Public affairs email is required")
    @Size(max = 100, message = "Public affairs email  must to exceed up to 100 character")
    private String publicAffairsEmail;

    // Address
    @NotBlank(message = "Street it required")
    @Size(max = 100, message = "Street must not exceed up to 100 character")
    private String street;

    @NotBlank(message = "Barangay it required")
    @Size(max = 100, message = "Barangay must not exceed up to 100 character")
    private String barangay;

    @NotBlank(message = "Street it required")
    @Size(max = 100, message = "Province must not exceed up to 100 character")
    private String city;

    @NotBlank(message = "Province it required")
    @Size(max = 100, message = "Street must not exceed up to 100 character")
    private String province;

    @NotBlank(message = "Region it required")
    @Size(max = 100, message = "Region must not exceed up to 100 character")
    private String region;

    @NotBlank(message = "Country it required")
    @Size(max = 100, message = "Country must not exceed up to 100 character")
    private String country;

    
    @NotBlank(message = "Postal code it required")
    @Size(max = 100, message = "Postal code must not exceed up to 100 character")
    private String postalCode;

    @NotBlank(message = "Password is required")
    @Pattern(
        regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
        message = "Password must be at least 8 characters, include uppercase, lowercase, number, and special character"
    )
    private String password;
}
