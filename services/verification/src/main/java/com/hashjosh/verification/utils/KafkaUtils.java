package com.hashjosh.verification.utils;

import com.hashjosh.constant.EventType;
import org.springframework.stereotype.Component;

@Component
public class KafkaUtils {

    // Helper method to determine the event type based on the status
    public String getEventType(String status) {
        return switch (status) {
            case "APPROVED_BY_MA" -> EventType.APPLICATION_APPROVED_BY_MA.name();
            case "REJECTED_BY_MA" -> EventType.APPLICATION_REJECTED_BY_MA.name();
            default -> "unknown-event";
        };
    }
}
