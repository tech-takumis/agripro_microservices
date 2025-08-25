package com.hashjosh.applicationservice.dto;

import com.fasterxml.jackson.databind.JsonNode;
import com.hashjosh.applicationservice.enums.FieldType;

public record ApplicationFieldsResponseDto(
        Long id,
        String key,
        String fieldName,
        FieldType fieldType,
        Boolean required,
        String defaultValue,
        JsonNode choices,
        String validationRegex
) {
}
