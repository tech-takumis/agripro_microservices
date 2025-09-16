package com.hashjosh.verification.exception;

import com.hashjosh.verification.dto.ExceptionResponse;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class VerificationException extends RuntimeException {
    private final LocalDateTime timestamp = LocalDateTime.now();
    private final String message;
    private final int statusCode;

    public VerificationException(String message, int statusCode) {
        super(message);
        this.message = message;
        this.statusCode = statusCode;
    }

    public ExceptionResponse getExceptionResponse() {
        return new ExceptionResponse(
                message,
                statusCode,
                timestamp,
                null
        );
    }
}
