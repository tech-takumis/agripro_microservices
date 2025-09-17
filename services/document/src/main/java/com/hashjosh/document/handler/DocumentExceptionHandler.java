package com.hashjosh.document.handler;


import com.hashjosh.document.exception.DocumentException;
import com.hashjosh.document.exception.DocumentNotFoundException;
import com.hashjosh.document.exception.ExceptionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class DocumentExceptionHandler {

    @ExceptionHandler(DocumentNotFoundException.class)
    public ResponseEntity<ExceptionResponse> documentNotFound(DocumentNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getException());
    }

    @ExceptionHandler(DocumentException.class)
    public ResponseEntity<ExceptionResponse> documentException(DocumentException ex) {
        return ResponseEntity.status(ex.getStatusCode()).body(ex.getExceptionResponse());
    }
}
