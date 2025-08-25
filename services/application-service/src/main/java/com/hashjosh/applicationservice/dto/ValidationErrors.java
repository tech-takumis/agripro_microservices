package com.hashjosh.applicationservice.dto;

public record ValidationErrors(
        String fieldName,
        String message
) {
}
