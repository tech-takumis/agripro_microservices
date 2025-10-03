package com.hashjosh.rsbsa;

import java.time.LocalDateTime;

public record ExceptionResponse(
        String message,
        int statusCode,
        LocalDateTime timestamp,
        String path
) {
}
