package com.hashjosh.insurance.exceptions;

import com.hashjosh.insurance.dto.ExceptionResponse;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class InsuranceException extends RuntimeException {
    private final LocalDateTime timestamp = LocalDateTime.now();
    private final String message;
    private final int statusCode;

    public InsuranceException(String message, int statusCode) {
        super(message);
        this.message = message;
        this.statusCode = statusCode;
    }

    public ExceptionResponse getExceptionResponse(){
        return new ExceptionResponse(
                message,
                statusCode,
                timestamp,
                null
        );
    }
}
