package com.hashjosh.insurance.handler;


import com.hashjosh.insurance.dto.ExceptionResponse;
import com.hashjosh.insurance.exceptions.ClaimNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
