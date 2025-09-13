package com.hashjosh.users.exception;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class UserException extends RuntimeException {
    private final LocalDateTime timestamp = LocalDateTime.now();
    private final String message;
    private final  int statusCode;
    public UserException(String message, int statusCode) {
        super(message);
        this.message = message;
        this.statusCode = statusCode;
    }

    public ExceptionResponse getExceptionResponse(){
        return new ExceptionResponse(
                this.message,
                this.statusCode,
                this.timestamp
        );
    }
}
