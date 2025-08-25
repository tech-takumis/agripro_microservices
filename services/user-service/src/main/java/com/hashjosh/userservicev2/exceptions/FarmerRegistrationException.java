package com.hashjosh.userservicev2.exceptions;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class FarmerRegistrationException extends  RuntimeException{
    private final LocalDateTime timestamp = LocalDateTime.now();
    private final String error;
    private final int status;

    public FarmerRegistrationException(String message, int status, String error){
        super(message);
        this.status = status;
        this.error = error;
    }

}
