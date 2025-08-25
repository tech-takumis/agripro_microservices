package com.hashjosh.applicationservice.dto;

import java.util.List;

public record ApplicationSectionResponseDto(
        Long id,
        String title,
        List<ApplicationFieldsResponseDto> fields
) {
}
