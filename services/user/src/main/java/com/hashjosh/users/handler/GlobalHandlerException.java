package com.hashjosh.users.handler;

import com.hashjosh.users.exception.ExceptionResponse;
import com.hashjosh.users.exception.TenantIdException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalHandlerException {

    @ExceptionHandler(TenantIdException.class)
    public ResponseEntity<ExceptionResponse> handleTenantIdNotFound(TenantIdException ex) {
        return ResponseEntity.status(ex.getStatusCode()).body(ex.getExceptionResponse());
    }
}
