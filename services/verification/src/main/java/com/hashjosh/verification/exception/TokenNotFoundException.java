package com.hashjosh.verification.exception;

import com.hashjosh.verification.dto.ExceptionResponse;

import java.time.LocalDateTime;

public class TokenNotFoundException extends RuntimeException {

    private final LocalDateTime timestamp = LocalDateTime.now();
    private String message;
    private  int statusCode;
    private String path;
    public TokenNotFoundException(String message, int statusCode, String path) {
        super(message);
        this.message = message;
        this.statusCode = statusCode;
        this.path = path;
    }


    public ExceptionResponse getExceptionResponse() {
        return  new ExceptionResponse(
                message,
                statusCode,
                timestamp,
                path
        );
    }
}
