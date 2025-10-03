package com.hashjosh.application.exceptions;

public class ClientBatchException extends RuntimeException {
    public ClientBatchException(String message) {
        super(message);
    }

    public ClientBatchException(String message, Throwable cause) {
        super(message, cause);
    }
}
