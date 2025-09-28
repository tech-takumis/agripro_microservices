package com.hashjosh.users.handler;

import com.hashjosh.users.exception.ExceptionResponse;
import com.hashjosh.users.exception.RsbsaException;
import com.hashjosh.users.exception.TenantIdException;
import com.hashjosh.users.exception.UserException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalHandlerException {

    @ExceptionHandler(TenantIdException.class)
    public ResponseEntity<ExceptionResponse> handleTenantIdNotFound(TenantIdException ex) {
        return ResponseEntity.status(ex.getStatusCode()).body(ex.getExceptionResponse());
    }

    @ExceptionHandler(UserException.class)
    public ResponseEntity<ExceptionResponse> handleUserException(UserException ex){
        return ResponseEntity.status(ex.getStatusCode()).body(ex.getExceptionResponse());
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<String> handleUsernameNotFound(UsernameNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(RsbsaException.class)
    public ResponseEntity<ExceptionResponse> handleRsbsaException(RsbsaException ex){
        return ResponseEntity.status(ex.getStatusCode()).body(ex.getExceptionResponse());
    }
}
