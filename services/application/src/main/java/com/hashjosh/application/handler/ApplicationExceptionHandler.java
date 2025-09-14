package com.hashjosh.application.handler;


import com.hashjosh.application.dto.ErrorResponse;
import com.hashjosh.application.dto.ExceptionResponse;
import com.hashjosh.application.exceptions.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class ApplicationExceptionHandler {

    @ExceptionHandler(ApplicationNotFoundException.class)
    public ResponseEntity<ExceptionResponse> applicationNotFound(ApplicationNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getExceptionResponse());
    }

    @ExceptionHandler(ApplicationNotSupportedDatatypeException.class)
    public ResponseEntity<Map<String,String>> applicationNotSupportedDatatype(ApplicationNotSupportedDatatypeException e) {
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                .body(Map.of("message", e.getMessage()));
    }

    @ExceptionHandler(FileUploadException.class)
    public ResponseEntity<ExceptionResponse> fileUpload(FileUploadException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getExceptionResponse());
    }

    @ExceptionHandler(InvalidStatusException.class)
    public ResponseEntity<ErrorResponse> invalidStatus(InvalidStatusException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getErrorResponse());
    }

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ExceptionResponse> applicationException(ApplicationException ex){
        return ResponseEntity.status(ex.getStatusCode()).body(ex.getExceptionResponse());
    }
}
