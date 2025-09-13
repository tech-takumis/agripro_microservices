package com.hashjosh.application.dto;

import java.util.List;

public record ApplicationTypeRequestDto(
        String name,
        String description,
        String layout,
        List<ApplicationSectionRequestDto> sections
) {
}
