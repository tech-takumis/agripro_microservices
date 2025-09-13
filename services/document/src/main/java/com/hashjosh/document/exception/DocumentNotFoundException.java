package com.hashjosh.document.exception;


import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class DocumentNotFoundException extends RuntimeException {
    private final LocalDateTime timestamp = LocalDateTime.now();
    private String message;
    private int statusCode;
    private String path;

    public DocumentNotFoundException(String message, int statusCode, String path) {
        super(message);
        this.message = message;
        this.statusCode = statusCode;
        this.path = path;
    }

    public ExceptionResponse getException(){
        return new ExceptionResponse(message, statusCode, path, timestamp);
    }
}
