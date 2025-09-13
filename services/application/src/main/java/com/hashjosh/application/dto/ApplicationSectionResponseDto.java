package com.hashjosh.application.dto;

import java.util.List;
import java.util.UUID;

public record ApplicationSectionResponseDto(
        UUID id,
        String title,
        List<ApplicationFieldsResponseDto> fields
) {
}
