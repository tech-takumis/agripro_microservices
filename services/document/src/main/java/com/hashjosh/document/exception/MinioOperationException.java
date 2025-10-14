package com.hashjosh.document.exception;

import org.springframework.http.HttpStatus;

public class MinioOperationException extends RuntimeException {
    private final int statusCode;

    public MinioOperationException(String message, int statusCode, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
