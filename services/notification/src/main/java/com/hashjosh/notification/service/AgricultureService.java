package com.hashjosh.notification.service;

import com.hashjosh.kafkacommon.agriculture.BatchCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AgricultureService {

    private final AgricultureNotificationService agricultureNotificationService;

    public void handleBatchCreatedEvent(BatchCreatedEvent event) {
        // Send notification about the new batch
        agricultureNotificationService.sendBatchCreatedNotification(event);
        log.info("Handled batch created event for batch id: {}", event.getId());
    }
}
