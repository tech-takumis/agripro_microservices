package com.hashjosh.application.exceptions;

import lombok.Getter;

@Getter
public class ApplicationNotSupportedDatatypeException extends RuntimeException {
    private final String message;
    public ApplicationNotSupportedDatatypeException(String message) {
        super(message);
        this.message = message;
    }
}
