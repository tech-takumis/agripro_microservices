package com.hashjosh.pcic.exception;

import com.hashjosh.pcic.dto.ExceptionResponse;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ApplicationNotFoundException extends RuntimeException {
    private LocalDateTime timestamp = LocalDateTime.now();
    private String message;
    private int statusCode;
    public ApplicationNotFoundException(String message, int statusCode) {
        super(message);
        this.message = message;
        this.statusCode = statusCode;
    }

    public ExceptionResponse getExceptionResponse(){
        return new ExceptionResponse(message,statusCode, timestamp);
    }
}
