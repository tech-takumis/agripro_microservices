package com.hashjosh.application.dto.validation;

public record ValidationErrors(
        String fieldName,
        String message
) {
}
