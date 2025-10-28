package com.hashjosh.application.dto.fields;

import com.fasterxml.jackson.databind.JsonNode;
import com.hashjosh.application.enums.FieldType;

import java.util.UUID;

public record ApplicationFieldsResponseDto(
        UUID id,
        String key,
        String fieldName,
        FieldType fieldType,
        Boolean required,
        String defaultValue,
        JsonNode choices,
        String validationRegex
) {
}
