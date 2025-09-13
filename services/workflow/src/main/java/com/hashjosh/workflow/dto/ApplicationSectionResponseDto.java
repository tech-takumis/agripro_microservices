package com.hashjosh.workflow.dto;

import java.util.List;
import java.util.UUID;

public record ApplicationSectionResponseDto(
        UUID id,
        String title,
        List<ApplicationFieldsResponseDto> fields
) {
}
