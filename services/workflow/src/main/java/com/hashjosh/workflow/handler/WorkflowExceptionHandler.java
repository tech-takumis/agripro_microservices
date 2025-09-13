package com.hashjosh.workflow.handler;

import com.hashjosh.workflow.dto.ExceptionResponseDto;
import com.hashjosh.workflow.exceptions.ApplicationNotFoundException;
import com.hashjosh.workflow.exceptions.UserNotFoundException;
import com.hashjosh.workflow.exceptions.WorkflowHistoryAlreadyExist;
import com.hashjosh.workflow.exceptions.WorkflowHistoryNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class WorkflowExceptionHandler {

    @ExceptionHandler(ApplicationNotFoundException.class)
    public ResponseEntity<ExceptionResponseDto> applicationNotFound(ApplicationNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getExceptionResponse());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ExceptionResponseDto> usernameNotFound(UserNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getExceptionResponse());
    }

    @ExceptionHandler(WorkflowHistoryAlreadyExist.class)
    public ResponseEntity<ExceptionResponseDto> workflowHistoryAlreadyExist(WorkflowHistoryAlreadyExist e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getExceptionResponse());
    }

    @ExceptionHandler(WorkflowHistoryNotFoundException.class)
    public ResponseEntity<ExceptionResponseDto> workflowHistoryNotFound(WorkflowHistoryNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getExceptionResponse());
    }
}
