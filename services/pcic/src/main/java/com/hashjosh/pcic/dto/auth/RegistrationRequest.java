package com.hashjosh.pcic.dto.auth;

import jakarta.validation.constraints.*;
import lombok.*;

import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistrationRequest {
    @NotBlank(message = "Username is required")
    @Size(max = 100,message = "Username must not exceed to 100 character")
    private String username;

    @NotBlank(message = "Firstname is required")
    @Size(max = 100,message = "Firstname must not exceed to 100 character")
    private String firstName;

    @NotBlank(message = "Lastname is required")
    @Size(max = 100,message = "Lastname must not exceed to 100 character")
    private String lastName;

    @NotBlank(message = "Password is required")
    private String password;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Phone number is required")
    @Size(max = 100, message = "Phone number must not exceed up to 100 character")
    @Pattern(
            regexp = "^\\+63\\d{10}$",
            message = "Phone number must start with +63 followed by 10 digits (e.g. +639123456789)"
    )
    private String phoneNumber;

    @NotBlank(message = "Address is required")
    @Size(max = 100,message = "Address must not exceed to 100 character")
    private String address;

    @NotEmpty(message = "PCIC must have at least one associated  role")
    private Set<UUID> rolesId;

    // Profile section
    @NotBlank(message = "Mandate is required")
    @Size(max = 100,message = "Mandate must not exceed to 100 character")
    private String mandate;

    @NotBlank(message = "Vision is required")
    @Size(max = 100,message = "Vision must not exceed to 100 character")
    private String vision;

    @NotBlank(message = "Mission is required")
    @Size(max = 100,message = "Mission must not exceed to 100 character")
    private String mission;

    @NotBlank(message = "Core values is required")
    @Size(max = 100,message = "Core values must not exceed to 100 character")
    private String coreValues;

    @NotBlank(message = "head office address is required")
    @Size(max = 100,message = "head office address must not exceed to 100 character")
    private String headOfficeAddress;

    @NotBlank(message = "Office phone number is required")
    @Size(max = 100,message = "Office phone number must not exceed to 100 character")
    private String phone;

    @NotBlank(message = "Official number is required")
    @Size(max = 100,message = "Official number must not exceed to 100 character")
    private String pcicEmail;

    @NotBlank(message = "Official website is required")
    @Size(max = 100,message = "Official website must not exceed to 100 character")
    private String website;
}
