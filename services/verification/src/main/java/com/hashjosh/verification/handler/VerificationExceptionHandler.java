package com.hashjosh.verification.handler;

import com.hashjosh.verification.dto.ExceptionResponse;
import com.hashjosh.verification.exception.ApplicationNotFoundException;
import com.hashjosh.verification.exception.TokenNotFoundException;
import com.hashjosh.verification.exception.VerificationException;
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
