package com.example.agriculture.dto;

import java.time.LocalDateTime;

public record ExceptionResponse(
        String message,
        int status,
        LocalDateTime timestamp
) {
}
