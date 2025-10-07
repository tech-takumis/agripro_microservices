package com.hashjosh.pcic.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
public class ErrorResponse {
    private int status;
    private String error;
    private Map<String,String> details;
    private LocalDateTime timestamp;
}
