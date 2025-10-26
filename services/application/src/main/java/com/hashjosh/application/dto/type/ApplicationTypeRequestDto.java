package com.hashjosh.application.dto.type;

import com.hashjosh.application.dto.sections.ApplicationSectionRequestDto;

import java.util.List;

public record ApplicationTypeRequestDto(
        String name,
        String description,

        String providerName,
        String layout,
        List<ApplicationSectionRequestDto> sections
) {
}
