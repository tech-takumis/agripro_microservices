package com.hashjosh.users.exception;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class RsbsaException extends RuntimeException {
    private final LocalDateTime timstamp = LocalDateTime.now();
    private final String message;
    private final int statusCode;
    public RsbsaException(String message, int statusCode) {
        super(message);
        this.message = message;
        this.statusCode = statusCode;
    }

    public ExceptionResponse getExceptionResponse(){
        return new ExceptionResponse(
                this.message,
                this.statusCode,
                this.timstamp
        );
    }
}
