package com.hashjosh.verification.exception;

import com.hashjosh.verification.dto.ExceptionResponse;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ApplicationNotFoundException extends RuntimeException {
    private LocalDateTime timestamp = LocalDateTime.now();
    private String message;
    private int statusCode;
    private String path;
    public ApplicationNotFoundException(String message, int statusCode, String path) {
        super(message);
        this.message = message;
        this.statusCode = statusCode;
        this.path = path;
    }

    public ExceptionResponse getExceptionResponse(){
        return new ExceptionResponse(message,statusCode, timestamp, path);
    }
}
