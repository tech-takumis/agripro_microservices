package com.hashjosh.realtimegatewayservice.dto;

import lombok.*;


@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationRequestDTO {
    private String title;
    private String message;
}
