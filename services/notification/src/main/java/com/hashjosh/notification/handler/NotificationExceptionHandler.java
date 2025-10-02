package com.hashjosh.notification.handler;

import com.hashjosh.notification.dto.ExceptionResponseDto;
import com.hashjosh.notification.exception.NotificationException;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Configuration
public class NotificationExceptionHandler{

    @ExceptionHandler(NotificationException.class)
    public ResponseEntity<ExceptionResponseDto> handleNotificationException(NotificationException ex){
        return ResponseEntity.status(ex.getStatusCode()).body(
                ExceptionResponseDto.builder()
                        .message(ex.getMessage())
                        .statusCode(ex.getStatusCode())
                        .timestamp(ex.getTimestamp())
                        .build()
        );
    }
}
