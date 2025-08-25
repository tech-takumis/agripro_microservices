package com.hashjosh.applicationservice.dto;

import java.util.List;

public record ApplicationTypeResponseDto(
        Long id,
        String name,
        String description,
        String layout,
        List<ApplicationSectionResponseDto> sections

) {
}
