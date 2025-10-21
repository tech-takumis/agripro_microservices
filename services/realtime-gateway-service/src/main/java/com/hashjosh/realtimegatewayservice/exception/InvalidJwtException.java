package com.hashjosh.realtimegatewayservice.exception;

import org.springframework.http.HttpStatus;

public class InvalidJwtException extends ApiException{

    public InvalidJwtException(String message) {
        super(message,  HttpStatus.UNAUTHORIZED);
    }
}
