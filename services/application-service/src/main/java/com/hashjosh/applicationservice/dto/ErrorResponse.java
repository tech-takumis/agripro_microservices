package com.hashjosh.applicationservice.dto;

import java.time.LocalDateTime;

public record ErrorResponse(
        int statusCode,
        String message,
        LocalDateTime timeStamp
) {
}
