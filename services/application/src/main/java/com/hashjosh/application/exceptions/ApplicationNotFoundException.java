package com.hashjosh.application.exceptions;


import com.hashjosh.application.dto.ExceptionResponse;

import java.time.LocalDateTime;

public class ApplicationNotFoundException extends RuntimeException {
    private final LocalDateTime timestamp = LocalDateTime.now();
    private  final String message;
    private final int statusCode;
    private final String path;

    public ApplicationNotFoundException(String message, int statusCode, String path ) {
        super(message);
        this.message = message;
        this.statusCode = statusCode;
        this.path = path;

    }

    public ExceptionResponse getExceptionResponse() {
        return new ExceptionResponse(
                message,
                statusCode,
                path,
                timestamp
        );
    }

}
