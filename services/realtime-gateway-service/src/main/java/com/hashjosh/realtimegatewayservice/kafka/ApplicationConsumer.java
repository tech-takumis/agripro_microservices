package com.hashjosh.realtimegatewayservice.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hashjosh.kafkacommon.application.*;
import com.hashjosh.realtimegatewayservice.clients.FarmerResponse;
import com.hashjosh.realtimegatewayservice.clients.FarmerServiceClient;
import com.hashjosh.realtimegatewayservice.service.EmailService;
import com.hashjosh.realtimegatewayservice.wrapper.ApplicationNotificationDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Slf4j
public class ApplicationConsumer {

    private final SimpMessagingTemplate messagingTemplate;
    private final FarmerServiceClient farmerServiceClient;
    private final EmailService emailService;

    @KafkaListener(topics = "application-lifecycle", groupId = "realtime-group")
    public void consumeApplicationEvents(@Payload Object event){
        try {
            ApplicationNotificationDTO notification = null;
            UUID receiverId = null;
            String emailSubject = null;
            String emailMessage = null;
            String recipientEmail = null;

            switch (event) {
                case ApplicationSubmittedEvent e -> {
                    receiverId = e.getUserId();
                    notification = ApplicationNotificationDTO.builder()
                            .id(e.getSubmissionId())
                            .title("Application Submitted")
                            .message("Your application has been submitted successfully.")
                            .time(e.getSubmittedAt() != null ? e.getSubmittedAt() : LocalDateTime.now())
                            .read(false)
                            .build();
                    emailSubject = "Application Received";
                    emailMessage = "Your application has been submitted successfully.";
                }
                case VerificationStartedEvent e -> {
                    receiverId = e.getUserId();
                    notification = ApplicationNotificationDTO.builder()
                            .id(e.getSubmissionId())
                            .title("Verification Started")
                            .message("Verification process has started for your application.")
                            .time(e.getStartedAt() != null ? e.getStartedAt() : LocalDateTime.now())
                            .read(false)
                            .build();
                    emailSubject = "Verification Started";
                    emailMessage = "Verification process has started for your application.";
                }
                case ApplicationUnderReviewEvent e -> {
                    receiverId = e.getUserId();
                    notification = ApplicationNotificationDTO.builder()
                            .id(e.getSubmissionId())
                            .title("Application Under Review")
                            .message("Your application is now under review.")
                            .time(e.getReviewStartedAt() != null ? e.getReviewStartedAt() : LocalDateTime.now())
                            .read(false)
                            .build();
                    emailSubject = "Application Under Review";
                    emailMessage = "Your application is now under review.";
                }
                case VerificationCompletedEvent e -> {
                    receiverId = e.getUserId();
                    notification = ApplicationNotificationDTO.builder()
                            .id(e.getSubmissionId())
                            .title("Verification Completed")
                            .message("Verification completed: " + e.getStatus())
                            .time(e.getVerifiedAt() != null ? e.getVerifiedAt() : LocalDateTime.now())
                            .read(false)
                            .build();
                    emailSubject = "Verification " + e.getStatus();
                    emailMessage = "Verification completed: " + e.getStatus();
                }
                case ApplicationSentToPcicEvent e -> {
                    receiverId = e.getUserId();
                    notification = ApplicationNotificationDTO.builder()
                            .id(e.getSubmissionId())
                            .title("Application Sent to PCIC")
                            .message("Your application has been sent to PCIC.")
                            .time(e.getSentAt() != null ? e.getSentAt() : LocalDateTime.now())
                            .read(false)
                            .build();
                    emailSubject = "Application Sent to PCIC";
                    emailMessage = "Your application has been sent to PCIC.";
                }
                case ApplicationReceivedByPcicEvent e -> {
                    receiverId = e.getUserId();
                    notification = ApplicationNotificationDTO.builder()
                            .id(e.getSubmissionId())
                            .title("Application Received by PCIC")
                            .message("PCIC has received your application. Status: " + e.getVerificationStatus())
                            .time(e.getReceivedAt() != null ? e.getReceivedAt() : LocalDateTime.now())
                            .read(false)
                            .build();
                    emailSubject = "Application Received by PCIC";
                    emailMessage = "PCIC has received your application. Status: " + e.getVerificationStatus();
                }
                case InspectionScheduledEvent e -> {
                    receiverId = e.getUserId();
                    notification = ApplicationNotificationDTO.builder()
                            .id(e.getSubmissionId())
                            .title("Inspection Scheduled")
                            .message("An inspection has been scheduled for your application.")
                            .time(e.getScheduledAt() != null ? e.getScheduledAt() : LocalDateTime.now())
                            .read(false)
                            .build();
                    emailSubject = "Inspection Scheduled";
                    emailMessage = "An inspection has been scheduled for your application.";
                }
                case InspectionCompletedEvent e -> {
                    receiverId = e.getUserId();
                    notification = ApplicationNotificationDTO.builder()
                            .id(e.getSubmissionId())
                            .title("Inspection Completed")
                            .message("Inspection for your application has been completed.")
                            .time(e.getInspectedAt() != null ? e.getInspectedAt() : LocalDateTime.now())
                            .read(false)
                            .build();
                    emailSubject = "Inspection Completed";
                    emailMessage = "Inspection for your application has been completed.";
                }
                case PolicyIssuedEvent e -> {
                    receiverId = e.getUserId();
                    notification = ApplicationNotificationDTO.builder()
                            .id(e.getSubmissionId())
                            .title("Policy Issued")
                            .message("A policy has been issued for your application. Policy #: " + e.getPolicyNumber())
                            .time(e.getIssuedAt() != null ? e.getIssuedAt() : LocalDateTime.now())
                            .read(false)
                            .build();
                    emailSubject = "Policy Issued: #" + e.getPolicyNumber();
                    emailMessage = "A policy has been issued for your application. Policy #: " + e.getPolicyNumber();
                }
                case ClaimProcessedEvent e -> {
                    receiverId = e.getUserId();
                    notification = ApplicationNotificationDTO.builder()
                            .id(e.getSubmissionId())
                            .title("Claim Processed")
                            .message("Your claim has been processed. Status: " + e.getPayoutStatus())
                            .time(e.getProcessedAt() != null ? e.getProcessedAt() : LocalDateTime.now())
                            .read(false)
                            .build();
                    emailSubject = "Claim Processed";
                    emailMessage = "Your claim has been processed. Status: " + e.getPayoutStatus();
                }
                default -> {
                    log.warn("Unknown event type: {}", event.getClass().getSimpleName());
                }
            }

            if (receiverId == null || notification == null) {
                log.warn("Could not extract receiverId or notification from event: {}", event.getClass().getSimpleName());
                return;
            }

            // WebSocket notification
            messagingTemplate.convertAndSendToUser(
                    receiverId.toString(),
                    "/queue/application.notifications",
                    notification
            );
            log.info("✅ Sent WebSocket notification to user {}", receiverId);

            // Email notification
            try {
                FarmerResponse farmer = farmerServiceClient.getFarmerById(receiverId);
                recipientEmail = farmer.getEmail();
                if (recipientEmail != null && !recipientEmail.isBlank()) {
                    emailService.sendEmail(recipientEmail, emailSubject, emailMessage, false);
                    log.info("✅ Sent email notification to {}", recipientEmail);
                } else {
                    log.warn("No email found for userId={}", receiverId);
                }
            } catch (Exception ex) {
                log.error("🚫 Failed to send email notification: {}", ex.getMessage(), ex);
            }

        } catch (Exception e) {
            log.error("🚫 Failed to send notifications: {}", e.getMessage(), e);
        }
    }
}
