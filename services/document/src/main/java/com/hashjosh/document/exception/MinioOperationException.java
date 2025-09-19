package com.hashjosh.document.exception;

import lombok.Getter;
import org.springframework.web.ErrorResponse;

import java.time.LocalDateTime;

@Getter
public class MinioOperationException extends RuntimeException {
    private final LocalDateTime timestamp = LocalDateTime.now();
    private final int statusCode;
    private final String message;

    public MinioOperationException(
            String message,
            int statusCode
    ) {
        super(message);
        this.statusCode = statusCode;
        this.message = message;
    }

}
