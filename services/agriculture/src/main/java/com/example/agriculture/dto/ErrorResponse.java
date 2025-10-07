package com.example.agriculture.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@AllArgsConstructor
public class ErrorResponse {
    int status;
    String error;
    private Map<String,String> details;
    LocalDateTime timestamp;
}
