package com.hashjosh.workflow.exceptions;

import com.hashjosh.workflow.dto.ExceptionResponseDto;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class WorkflowHistoryAlreadyExist extends RuntimeException {
    private final LocalDateTime timestamp = LocalDateTime.now();
    private String message;
    private int status;

    public WorkflowHistoryAlreadyExist(String message, int statusCode) {
        super(message);
        this.message = message;
        this.status = statusCode;
    }

    public ExceptionResponseDto getExceptionResponse() {
        return new ExceptionResponseDto(
                message,
                status,
                timestamp,
                "In application event consumer"
        );
    }
}
