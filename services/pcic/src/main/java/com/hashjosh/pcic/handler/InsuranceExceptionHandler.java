package com.hashjosh.pcic.handler;


import com.hashjosh.pcic.dto.ErrorResponse;
import com.hashjosh.pcic.dto.ExceptionResponse;
import com.hashjosh.pcic.exception.ClaimNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class InsuranceExceptionHandler {

    @ExceptionHandler(ClaimNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleClaimNotFoundException(ClaimNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getExceptionResponse());
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        Map<String,String> fieldErrors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach((fieldError) -> {
            fieldErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
        });

        ErrorResponse response = new ErrorResponse();
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setError("Invalid arguments");
        response.setDetails(fieldErrors);
        response.setTimestamp(LocalDateTime.now());

        return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
