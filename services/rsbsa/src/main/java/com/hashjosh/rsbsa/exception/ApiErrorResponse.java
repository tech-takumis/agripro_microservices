package com.hashjosh.rsbsa.exception;

import lombok.*;

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
