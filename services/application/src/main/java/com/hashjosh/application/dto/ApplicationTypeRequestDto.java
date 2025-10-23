package com.hashjosh.application.dto;

import com.hashjosh.application.annotation.RecipientTypeSubset;
import com.hashjosh.constant.application.RecipientType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public record ApplicationTypeRequestDto(
        @NotBlank(message = "Name is required")
        @Size(min = 3, max = 100)
        String name,
        String description,

        @NotBlank(message = "Recipient type is required")
        @RecipientTypeSubset(anyOf = {RecipientType.AGRICULTURE, RecipientType.PCIC})
        RecipientType recipient,
        String layout,
        List<ApplicationSectionRequestDto> sections
) {
}
