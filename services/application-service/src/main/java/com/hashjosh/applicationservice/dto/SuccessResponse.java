package com.hashjosh.applicationservice.dto;

public record SuccessResponse (
        int statusCode,
        String message,
        Object data
) {
}
