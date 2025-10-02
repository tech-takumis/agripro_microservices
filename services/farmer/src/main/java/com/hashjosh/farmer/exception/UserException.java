package com.hashjosh.farmer.exception;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class UserException extends RuntimeException{

    private final LocalDateTime timestamp = LocalDateTime.now();
    private final String message;
    private final int status;

    public UserException(String message, int status) {
        super(message);
        this.message = message;
        this.status = status;
    }
}
