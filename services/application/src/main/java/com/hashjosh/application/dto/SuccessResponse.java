package com.hashjosh.application.dto;

public record SuccessResponse (
        int statusCode,
        String message,
        Object data
) {
}
