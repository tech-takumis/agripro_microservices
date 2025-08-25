package com.hashjosh.userservicev2.exceptions;

import java.time.LocalDateTime;

public class UserNotAuthenticatedException extends RuntimeException {
    private final LocalDateTime timestamp = LocalDateTime.now();

    public UserNotAuthenticatedException(String message, String error, int status) {
        super(message);
    }
}
