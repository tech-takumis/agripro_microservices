package com.hashjosh.realtimegatewayservice.wrapper;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationNotificationDTO {
    private UUID id;
    private String title;
    private String message;
    private LocalDateTime time;
    private boolean read;
}
