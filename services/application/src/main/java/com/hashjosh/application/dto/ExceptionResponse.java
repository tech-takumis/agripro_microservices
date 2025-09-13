package com.hashjosh.application.dto;

import java.time.LocalDateTime;

public record ExceptionResponse(
        String message,
        int statusCode,
        String path,
        LocalDateTime timestamp
) {
}
