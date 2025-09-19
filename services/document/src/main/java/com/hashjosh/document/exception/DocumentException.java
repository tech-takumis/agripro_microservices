package com.hashjosh.document.exception;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class DocumentException extends RuntimeException {

    private final LocalDateTime timestamp = LocalDateTime.now();
    private final String message;
    private final int statusCode;

    public DocumentException(String message, int statusCode) {
        super(message);
        this.message = message;
        this.statusCode = statusCode;
    }

    public ExceptionResponse getExceptionResponse(){
        return new ExceptionResponse(message, statusCode, null, timestamp);
    }

}
