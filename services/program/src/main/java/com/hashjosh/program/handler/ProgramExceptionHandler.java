package com.hashjosh.program.handler;

import com.hashjosh.program.dto.ExceptionResponse;
import com.hashjosh.program.exception.ProgramException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ProgramExceptionHandler {

    @ExceptionHandler(ProgramException.class)
    public ResponseEntity<ExceptionResponse> handleProgramException(ProgramException e) {
        return ResponseEntity.status(e.getStatusCode()).body(e.getExceptionResponse());
    }
}
