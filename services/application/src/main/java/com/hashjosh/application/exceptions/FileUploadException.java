package com.hashjosh.application.exceptions;

import com.hashjosh.application.dto.ExceptionResponse;
import lombok.Getter;

import java.time.LocalDateTime;


@Getter
public class FileUploadException extends RuntimeException {
    private final LocalDateTime timestamp = LocalDateTime.now();
    private final String message;
    private final int statusCode;
    private final String path;

    public FileUploadException(String message, int statusCode, String path) {
        super(message);
        this.statusCode = statusCode;
        this.path = path;
        this.message = message;
    }


    public ExceptionResponse getExceptionResponse(){
            return new ExceptionResponse(message, statusCode, path, timestamp);
    }
}
