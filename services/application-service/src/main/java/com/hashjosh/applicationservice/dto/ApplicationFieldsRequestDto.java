package com.hashjosh.applicationservice.dto;

import com.fasterxml.jackson.databind.JsonNode;
import com.hashjosh.applicationservice.enums.FieldType;

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
