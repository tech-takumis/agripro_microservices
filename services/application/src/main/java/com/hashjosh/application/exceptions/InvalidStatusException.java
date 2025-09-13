package com.hashjosh.application.exceptions;


import com.hashjosh.application.dto.ErrorResponse;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class InvalidStatusException extends RuntimeException {
    private final LocalDateTime timestamp = LocalDateTime.now();
    private String message;
    private int status;
    private String path;

    public InvalidStatusException(String message, int statusCode, String path) {
        super(message);
        this.message = message;
        this.status = statusCode;
        this.path = path;
    }

    public ErrorResponse getErrorResponse() {
        return new ErrorResponse(
                this.message,
                this.status,
                this.timestamp,
                this.path
        );
    };
}
