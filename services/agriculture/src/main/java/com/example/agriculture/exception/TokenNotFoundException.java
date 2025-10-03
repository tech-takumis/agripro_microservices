package com.example.agriculture.exception;


import com.example.agriculture.dto.ExceptionResponse;

import java.time.LocalDateTime;

public class TokenNotFoundException extends RuntimeException {

    private final LocalDateTime timestamp = LocalDateTime.now();
    private String message;
    private  int statusCode;
    public TokenNotFoundException(String message, int statusCode) {
        super(message);
        this.message = message;
        this.statusCode = statusCode;
    }


    public ExceptionResponse getExceptionResponse() {
        return  new ExceptionResponse(
                message,
                statusCode,
                timestamp
        );
    }
}
