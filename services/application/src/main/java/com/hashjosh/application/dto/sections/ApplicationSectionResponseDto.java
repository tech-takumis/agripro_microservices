package com.hashjosh.application.dto.sections;

import com.hashjosh.application.dto.fields.ApplicationFieldsResponseDto;

import java.util.List;
import java.util.UUID;

public record ApplicationSectionResponseDto(
        UUID id,
        String title,
        List<ApplicationFieldsResponseDto> fields
) {
}
