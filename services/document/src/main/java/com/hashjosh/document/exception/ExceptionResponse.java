package com.hashjosh.document.exception;

import java.time.LocalDateTime;

public record ExceptionResponse(
        String message,
        int statusCode,
        String path,
        LocalDateTime timestamp
) {
}
