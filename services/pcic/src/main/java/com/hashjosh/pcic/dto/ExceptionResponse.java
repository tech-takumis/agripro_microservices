package com.hashjosh.pcic.dto;

import java.time.LocalDateTime;

public record ExceptionResponse(
        String message,
        int status,
        LocalDateTime timestamp
) {
}
