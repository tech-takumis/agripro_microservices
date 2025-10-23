package com.hashjosh.realtimegatewayservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailNotificationPayload {
    private String subject;
    private String message;
    private String attachmentUrl;
}
