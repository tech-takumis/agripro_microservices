package com.hashjosh.application.dto.fields;

import com.fasterxml.jackson.databind.JsonNode;

public record ApplicationFieldsRequestDto(
        String key,
        String fieldName,
        String fieldType,
        Boolean required,
        String defaultValue,
        JsonNode choices,
        String validationRegex
) {
}
