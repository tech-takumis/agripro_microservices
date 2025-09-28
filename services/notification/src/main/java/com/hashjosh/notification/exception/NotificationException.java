package com.hashjosh.notification.exception;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class NotificationException extends RuntimeException {
    private final   String message;
    private final   int statusCode;
    private final    LocalDateTime timestamp = LocalDateTime.now();

    public NotificationException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
        this.message = message;
    }
}
