package com.hashjosh.pcic.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class RegistrationResponse {
    private String username;
    private String message;
    private boolean success;
    private int status;
    private LocalDateTime timestamp;
}
