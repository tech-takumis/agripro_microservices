package com.hashjosh.rsbsa.handler;

import com.hashjosh.rsbsa.dto.ExceptionResponse;
import com.hashjosh.rsbsa.exception.RsbsaNotFoundException;
import com.hashjosh.rsbsa.exception.RsbsaException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RsbsaExceptionHandler {

    @ExceptionHandler(RsbsaNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleNotFoundException(RsbsaNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getExceptionResponse());
    }

    @ExceptionHandler(RsbsaException.class)
    public ResponseEntity<ExceptionResponse> handleRsbsaException(RsbsaException ex){
        return ResponseEntity.status(ex.getStatusCode()).body(ex.getExceptionResponse());
    }
}
