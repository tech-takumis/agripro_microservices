package com.hashjosh.application.dto;

public record ValidationErrors(
        String fieldName,
        String message
) {
}
