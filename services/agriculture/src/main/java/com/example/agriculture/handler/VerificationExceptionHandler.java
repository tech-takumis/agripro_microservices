package com.example.agriculture.handler;

import com.example.agriculture.dto.ExceptionResponse;
import com.example.agriculture.exception.ApplicationNotFoundException;
import com.example.agriculture.exception.TokenNotFoundException;
import com.example.agriculture.exception.VerificationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class VerificationExceptionHandler {

    @ExceptionHandler(ApplicationNotFoundException.class)
    public ResponseEntity<ExceptionResponse> applicationNotFound(ApplicationNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getExceptionResponse());
    }

    @ExceptionHandler(TokenNotFoundException.class)
    public ResponseEntity<ExceptionResponse> tokenNotFound(TokenNotFoundException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getExceptionResponse());
    }

    @ExceptionHandler(VerificationException.class)
    public ResponseEntity<ExceptionResponse> verificationException(VerificationException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getExceptionResponse());
    }


}
