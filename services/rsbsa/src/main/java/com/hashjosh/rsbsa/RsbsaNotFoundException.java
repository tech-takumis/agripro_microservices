package com.hashjosh.rsbsa;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class RsbsaNotFoundException extends RuntimeException {
    private final LocalDateTime timestamp = LocalDateTime.now();
    private String message;
    private int statusCode;
    private String path;
    public RsbsaNotFoundException(String message, int statusCode, String path) {
        super(message);
        this.message = message;
        this.statusCode = statusCode;
        this.path = path;
    }

    public ExceptionResponse getExceptionResponse(){
        return new ExceptionResponse(
            this.message,
                this.statusCode,
                this.timestamp,
                this.path
        );
    }
}
