package com.hashjosh.pcic.dto.auth;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistrationResponse {
    private String username;
    private String message;
    private boolean success;
    private int status;
    private LocalDateTime timestamp;
}
