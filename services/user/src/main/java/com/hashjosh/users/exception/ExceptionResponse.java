package com.hashjosh.users.exception;

import java.time.LocalDateTime;

public record ExceptionResponse(
        String message,
        int status,
        LocalDateTime timestamp
) {
}
