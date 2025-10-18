package com.example.agriculture.handler;

import com.example.agriculture.dto.ErrorResponse;
import com.example.agriculture.dto.ExceptionResponse;
import com.example.agriculture.exception.BatchException;
import com.example.agriculture.exception.TokenNotFoundException;
import com.example.agriculture.exception.VerificationException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class VerificationExceptionHandler {


    @ExceptionHandler(TokenNotFoundException.class)
    public ResponseEntity<ExceptionResponse> tokenNotFound(TokenNotFoundException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getExceptionResponse());
    }

    @ExceptionHandler(VerificationException.class)
    public ResponseEntity<ExceptionResponse> verificationException(VerificationException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getExceptionResponse());
    }

    @ExceptionHandler(BatchException.class)
    public ResponseEntity<String> batchException(BatchException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> methodArgumentNotValidException(MethodArgumentNotValidException ex) {
        Map<String,String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach((fieldError) -> {
            fieldErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
        });

        ErrorResponse response = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                "Invalid Arguments",
                fieldErrors,
                LocalDateTime.now()
        );

        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(EntityNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "success", false,
                "message", ex.getMessage()
        ));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleBadRequest(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "success", false,
                "message", ex.getMessage()
        ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "An unexpected error occurred: " + ex.getMessage()
        ));
    }
}
