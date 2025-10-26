package com.hashjosh.application.dto.type;

import com.hashjosh.application.dto.sections.ApplicationSectionResponseDto;

import java.util.List;
import java.util.UUID;

public record ApplicationTypeResponseDto(
        UUID id,
        String name,

        String description,
        String layout,
        List<ApplicationSectionResponseDto> sections

) {
}
