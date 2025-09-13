package com.hashjosh.insurance.exceptions;

import com.hashjosh.insurance.dto.ExceptionResponse;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class ClaimNotFoundException extends RuntimeException {
    private final LocalDateTime timestamp = LocalDateTime.now();
    private String message;
    private int statusCode;
    private String path;

    public ClaimNotFoundException(String message, int statusCode, String path) {
        super(message);
        this.message = message;
        this.statusCode = statusCode;
        this.path = path;
    }

    public ExceptionResponse getExceptionResponse(){
        return new ExceptionResponse(message, statusCode,timestamp, path);
    }
}
