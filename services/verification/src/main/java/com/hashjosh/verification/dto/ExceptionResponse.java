package com.hashjosh.verification.dto;

import java.time.LocalDateTime;

public record ExceptionResponse(
        String message,
        int status,
        LocalDateTime timestamp,
        String path
) {
}
