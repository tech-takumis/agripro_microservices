package com.hashjosh.userservicev2.exceptions;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class RsbsaNotFoundException extends  RuntimeException{
    private final LocalDateTime timestamp = LocalDateTime.now();
    private final int status;
    private final String error;

    public RsbsaNotFoundException(String message, int status, String error){
        super(message);
        this.status = status;
        this.error = error;
    }

}
