package com.hashjosh.applicationservice.dto;

import java.util.List;

public record ApplicationSectionRequestDto(
        String title,
        List<ApplicationFieldsRequestDto> fields
) {
}
