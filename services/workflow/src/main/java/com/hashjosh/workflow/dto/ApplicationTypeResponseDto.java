package com.hashjosh.workflow.dto;

import java.util.List;
import java.util.UUID;

public record ApplicationTypeResponseDto (
        UUID id,
        String name,
        String description,
        String layout,
        List<ApplicationSectionResponseDto> sections
){
}
