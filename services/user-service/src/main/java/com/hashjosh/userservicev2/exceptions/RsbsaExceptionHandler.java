package com.hashjosh.userservicev2.exceptions;

import com.hashjosh.userservicev2.dto.ErrorResponseDto;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RsbsaExceptionHandler {

    @ExceptionHandler(RsbsaNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleRsbsaNotFoundException(RsbsaNotFoundException ex){
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponseDto(ex.getTimestamp(),
                        ex.getStatus(), ex.getError(),
                        ex.getMessage()));
    }
}
