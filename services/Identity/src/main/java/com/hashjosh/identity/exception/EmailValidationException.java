package com.hashjosh.identity.exception;

import lombok.Getter;

import java.util.List;

@Getter
public class EmailValidationException extends RuntimeException {
    private final List<String> violations;

    public EmailValidationException(List<String> violations) {
        super("Email validation failed: " + String.join(", ", violations));
        this.violations = violations;
    }
}

