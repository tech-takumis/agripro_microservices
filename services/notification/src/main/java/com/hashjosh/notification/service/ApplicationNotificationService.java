package com.hashjosh.notification.service;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hashjosh.kafkacommon.application.*;
import com.hashjosh.notification.clients.FarmerResponse;
import com.hashjosh.notification.clients.FarmerServiceClient;
import com.hashjosh.notification.entity.Notification;
import com.hashjosh.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApplicationNotificationService {
    private final SpringTemplateEngine templateEngine;
    private final NotificationRepository notificationRepository;
    private final FarmerServiceClient farmerServiceClient;
    private final ObjectMapper objectMapper;
    private final EmailService emailService;

    @KafkaListener(topics = "application-lifecycle", groupId = "notification-group")
    public void handleLifecycleEvent(ConsumerRecord<String, Object> record) {
        Object event = record.value();
        UUID userId = getUserIdFromEvent(event);

        FarmerResponse farmer = farmerServiceClient.getFarmerById(userId);

        String recipientEmail = farmer.getEmail();
        String subject = getNotificationSubject(event);
        String template = getNotificationTemplate(event);
        String content = renderTemplate(event, template, farmer);

        JsonNode payload = objectMapper.convertValue(event, JsonNode.class);
        Notification<Object> notification = Notification.builder()
                .recipient(recipientEmail)
                .type("EMAIL")
                .status("PENDING")
                .title(subject)
                .payload(payload)
                .createdAt(LocalDateTime.now())
                .template(template)
                .build();
        notificationRepository.save(notification);

        try {
            emailService.sendEmail(recipientEmail, subject, content, true);
            notification.setStatus("SENT");
            notification.setUpdatedAt(LocalDateTime.now());
            log.info("Sent notification to {}", recipientEmail);
        } catch (RuntimeException e) {
            notification.setStatus("FAILED");
            notification.setUpdatedAt(LocalDateTime.now());
            log.error("Failed to send notification to {}", recipientEmail, e);
        }
        notificationRepository.save(notification);
    }

    private String getNotificationSubject(Object event) {
        return switch (event) {
            case ApplicationSubmittedEvent e -> "Application Received";
            case VerificationStartedEvent e -> "Verification Started";
            case ApplicationUnderReviewEvent e -> "Application Under Review";
            case VerificationCompletedEvent e -> "Verification " + e.getStatus();
            case ApplicationSentToPcicEvent e -> "Application Sent to PCIC";
            case ApplicationReceivedByPcicEvent e -> "Application Received by PCIC";
            case InspectionScheduledEvent e -> "Inspection Scheduled";
            case InspectionCompletedEvent e -> "Inspection Completed";
            case PolicyIssuedEvent e -> "Policy Issued: #" + e.getPolicyNumber();
            case ClaimProcessedEvent e -> "Claim Processing Started";
            default -> "Application Update";
        };
    }

    private String getNotificationTemplate(Object event) {
        return switch (event) {
            case ApplicationSubmittedEvent e -> "email/application-submitted";
            case VerificationStartedEvent e -> "email/verification-started";
            case ApplicationUnderReviewEvent e -> "email/application-under-review";
            case VerificationCompletedEvent e -> "email/verification-completed";
            case ApplicationSentToPcicEvent e -> "email/application-sent-to-pcic";
            case ApplicationReceivedByPcicEvent e -> "email/application-received-by-pcic";
            case InspectionScheduledEvent e -> "email/inspection-scheduled";
            case InspectionCompletedEvent e -> "email/inspection-completed";
            case PolicyIssuedEvent e -> "email/policy-issued";
            case ClaimProcessedEvent e -> "email/claim-processed";
            default -> "email/generic";
        };
    }

    private String renderTemplate(Object event, String template, FarmerResponse user) {
        Context context = new Context();
        context.setVariable("email", user.getEmail());
        context.setVariable("name", user.getFirstName() + " " + user.getLastName());
        context.setVariable("event", event);
        return templateEngine.process(template, context);
    }

    private UUID getUserIdFromEvent(Object event) {
        return switch (event) {
            case ApplicationSubmittedEvent e -> e.getUserId();
            case VerificationStartedEvent e -> e.getUserId();
            case ApplicationUnderReviewEvent e -> e.getUserId();
            case VerificationCompletedEvent e -> e.getUserId();
            case ApplicationSentToPcicEvent e -> e.getUserId();
            case ApplicationReceivedByPcicEvent e -> e.getUserId();
            case InspectionScheduledEvent e -> e.getUserId();
            case InspectionCompletedEvent e -> e.getUserId();
            case PolicyIssuedEvent e -> e.getUserId();
            case ClaimProcessedEvent e -> e.getUserId();
            default -> throw new IllegalArgumentException("Unknown event type");
        };
    }


}
