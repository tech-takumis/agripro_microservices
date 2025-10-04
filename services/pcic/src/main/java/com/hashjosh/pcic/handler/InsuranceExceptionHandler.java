package com.hashjosh.pcic.handler;


import com.hashjosh.pcic.dto.ExceptionResponse;
import com.hashjosh.pcic.exception.ClaimNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class InsuranceExceptionHandler {

    @ExceptionHandler(ClaimNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleClaimNotFoundException(ClaimNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getExceptionResponse());
    }

}
