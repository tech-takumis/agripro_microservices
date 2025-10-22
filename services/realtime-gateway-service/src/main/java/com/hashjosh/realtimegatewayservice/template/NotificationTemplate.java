package com.hashjosh.realtimegatewayservice.template;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationTemplate {

    private String title;
    private String message;
    private LocalDateTime timestamp;
}
