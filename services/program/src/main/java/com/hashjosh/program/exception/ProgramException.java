package com.hashjosh.program.exception;

import com.hashjosh.program.dto.ExceptionResponse;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ProgramException extends RuntimeException {
    private final LocalDateTime timestamp = LocalDateTime.now();
    private final String message;
    private final int statusCode;

    public ProgramException(String message, int statusCode) {
        super(message);
        this.message = message;
        this.statusCode = statusCode;
    }

    public ExceptionResponse getExceptionResponse() {
        return ExceptionResponse.builder()
                .message(message)
                .statusCode(statusCode)
                .timestamp(timestamp)
                .build();
    }
}
