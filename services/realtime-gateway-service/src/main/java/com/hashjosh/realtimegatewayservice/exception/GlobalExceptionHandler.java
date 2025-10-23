package com.hashjosh.realtimegatewayservice.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiErrorResponse> handleApiException(ApiException ex) {
        log.warn("Handled API exception: {}", ex.getMessage());

        return ResponseEntity
                .status(ex.getStatus())
                .body(ApiErrorResponse.builder()
                        .success(false)
                        .message(ex.getMessage())
                        .status(ex.getStatus().value())
                        .timestamp(Instant.now())
                        .build());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnexpected(Exception ex) {
        log.error("Unhandled exception", ex);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiErrorResponse.builder()
                        .success(false)
                        .message("An unexpected error occurred. Please contact support.")
                        .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .timestamp(Instant.now())
                        .details(Map.of("error", ex.getMessage()))
                        .build());
    }
}
