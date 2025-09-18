package com.hashjosh.workflow.exceptions;

import java.time.LocalDateTime;

public class WorkflowException extends RuntimeException {
    private final LocalDateTime timestamp = LocalDateTime.now();
    private final String message;
    private final int statusCode;
    public WorkflowException(String message, int statusCode) {
        super(message);
        this.message = message;
        this.statusCode = statusCode;
    }
}
