package com.hashjosh.identity.exception;

import lombok.Getter;

import java.util.List;

@Getter
public class PasswordValidationException extends RuntimeException {
    private final List<String> violations;

    public PasswordValidationException(List<String> violations) {
        super("Password validation failed: " + String.join(", ", violations));
        this.violations = violations;
    }
}

