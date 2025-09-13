package com.hashjosh.workflow.dto;

import java.time.LocalDateTime;

public record ExceptionResponseDto(
        String message,
        int statusCode,
        LocalDateTime timestamp,
        String path
) {
}
