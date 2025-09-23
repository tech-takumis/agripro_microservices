package com.hashjosh.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SMSNotificationPayload {
    private String phoneNumber;
    private String message;
}
