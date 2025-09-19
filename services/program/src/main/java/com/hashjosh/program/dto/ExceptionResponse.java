package com.hashjosh.program.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@Data
public class ExceptionResponse {
    private String message;
    private int statusCode;
    private LocalDateTime timestamp;
}
