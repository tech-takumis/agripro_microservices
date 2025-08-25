package com.hashjosh.userservicev2.exceptions;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class UserAlreadyExistException extends RuntimeException {

    private final LocalDateTime timestamp = LocalDateTime.now();
    private final int status;
    private final String error;

    public UserAlreadyExistException(String message, int status, String error){
        super(message);
        this.status = status;
        this.error = error;
    }
}
