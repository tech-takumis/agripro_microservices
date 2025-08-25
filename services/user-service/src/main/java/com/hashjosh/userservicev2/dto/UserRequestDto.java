package com.hashjosh.userservicev2.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.*;

public record UserRequestDto(
        @NotNull(message = "Firstname is required")
        @Size(min = 3, max = 50, message = "Firstname must be between 3 and 50 characters")
        String firstName,

        @NotNull(message = "Lastname is required")
        @Size(min = 3, max = 50, message = "Lastname must be between 3 and 50 characters")
        String lastName,

        @Email(message = "Email should be a valid!")
        @NotNull(message = "Email is required!")
        @Column(unique = true)
        String email,

        @NotNull(message = "Gender is required")
        @Size(min = 3, max = 50, message = "Gender must be between 3 and 50 characters")
        String gender,

        @NotBlank(message = "Contact number is required")
        String contactNumber,

        @NotBlank(message = "Civil Status is required")
        String civilStatus,

        @NotBlank(message = "Address is required!")
        String address,

        Long roleId,

        UserProfileRequest userProfile
) {
}
