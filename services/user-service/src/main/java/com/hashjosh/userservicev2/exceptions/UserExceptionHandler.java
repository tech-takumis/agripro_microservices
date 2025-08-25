package com.hashjosh.userservicev2.exceptions;

import com.hashjosh.userservicev2.dto.ErrorResponseDto;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class UserExceptionHandler {


    @ExceptionHandler(FarmerRegistrationException.class)
    public ResponseEntity<ErrorResponseDto> farmerRegistrationException(FarmerRegistrationException ex){
        return ResponseEntity
                .status(ex.getStatus())
                .body(new ErrorResponseDto(
                        ex.getTimestamp(),
                        ex.getStatus(),
                        ex.getError(),
                        ex.getMessage()
                ));
    }

    @ExceptionHandler(UserAlreadyExistException.class)
    public ResponseEntity<ErrorResponseDto> handleUserAlreadyExistException(UserAlreadyExistException ex){
        return ResponseEntity
                .status(ex.getStatus())
                .body(new ErrorResponseDto(
                        ex.getTimestamp(),
                        ex.getStatus(),
                        ex.getError(),
                        ex.getMessage()
                ));
    }
}
