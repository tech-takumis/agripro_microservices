package com.hashjosh.application.dto.sections;

import com.hashjosh.application.dto.fields.ApplicationFieldsRequestDto;

import java.util.List;

public record ApplicationSectionRequestDto(
        String title,
        List<ApplicationFieldsRequestDto> fields
) {
}
