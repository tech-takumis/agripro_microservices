package com.hashjosh.notification.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
public class ExceptionResponseDto {

    String message;
    int statusCode;
    LocalDateTime timestamp;
}
