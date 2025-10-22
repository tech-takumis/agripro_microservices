package com.hashjosh.application.exceptions;

import com.hashjosh.application.dto.ValidationError;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.List;

@Getter
public class ApiException extends RuntimeException {
    private final HttpStatus status;
    private List<ValidationError> errors;


    public ApiException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public ApiException(String message,List<ValidationError> errors, HttpStatus status) {
        super(message);
        this.errors = errors;
        this.status = status;
    }


    public static ApiException notFound(String message) {
        return new ApiException(message, HttpStatus.NOT_FOUND);
    }

    public static ApiException badRequest(String message) {
        return new ApiException(message, HttpStatus.BAD_REQUEST);
    }

    public static ApiException conflict(String message) {
        return new ApiException(message, HttpStatus.CONFLICT);
    }

    public static ApiException unauthorized(String message) {
        return new ApiException(message, HttpStatus.UNAUTHORIZED);
    }

    public static ApiException internalError(String message) {
        return new ApiException(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
