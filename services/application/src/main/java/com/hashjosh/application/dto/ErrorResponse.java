package com.hashjosh.application.dto;

import java.time.LocalDateTime;

public record ErrorResponse(
        String message,
        int statusCode,
        LocalDateTime timeStamp,
        String path
) {
}
