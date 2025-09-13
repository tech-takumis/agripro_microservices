package com.hashjosh.workflow.exceptions;

import com.hashjosh.workflow.dto.ExceptionResponseDto;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ApplicationNotFoundException extends RuntimeException {
    private  final LocalDateTime timestamp = LocalDateTime.now();
    private  String message;
    private int statusCode;
    private String path;
    public ApplicationNotFoundException(String message, int statusCode, String path) {
        super(message);
        this.message = message;
        this.statusCode = statusCode;
        this.path = path;
    }

    public ExceptionResponseDto getExceptionResponse(){
        return new ExceptionResponseDto(
            this.message,
                this.statusCode,
                this.timestamp,
                this.path
        );
    }
}
