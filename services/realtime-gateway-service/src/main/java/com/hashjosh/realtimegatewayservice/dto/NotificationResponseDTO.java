package com.hashjosh.realtimegatewayservice.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationResponseDTO {
    private UUID id;
    private String title;
    private String message;
    private LocalDateTime time;
    private boolean read;
}
