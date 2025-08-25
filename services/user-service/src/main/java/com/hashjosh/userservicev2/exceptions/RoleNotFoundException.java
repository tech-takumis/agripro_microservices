package com.hashjosh.userservicev2.exceptions;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class RoleNotFoundException extends RuntimeException{
    private final LocalDateTime timestamp = LocalDateTime.now();
    private final String error;
    private final int status;

    public RoleNotFoundException(String message, String error, int status) {
        super(message);
        this.error = error;
        this.status = status;
    }
}
