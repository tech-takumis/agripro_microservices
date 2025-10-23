package com.example.agriculture.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiErrorResponse {
    private boolean success;
    private String message;
    private int status;
    private Instant timestamp;
    private Map<String, Object> details;
}
