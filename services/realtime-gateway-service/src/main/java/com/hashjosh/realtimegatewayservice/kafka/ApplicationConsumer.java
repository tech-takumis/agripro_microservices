package com.hashjosh.realtimegatewayservice.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hashjosh.kafkacommon.application.*;
import com.hashjosh.realtimegatewayservice.clients.FarmerResponse;
import com.hashjosh.realtimegatewayservice.clients.FarmerServiceClient;
import com.hashjosh.realtimegatewayservice.entity.Notification;
import com.hashjosh.realtimegatewayservice.repository.NotificationRepository;
import com.hashjosh.realtimegatewayservice.service.EmailService;
import com.hashjosh.realtimegatewayservice.wrapper.ApplicationNotificationDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
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
    private final NotificationRepository notificationRepository;

    @KafkaListener(topics = "application-lifecycle", groupId = "realtime-group-app-submitted")
    public void listenApplicationSubmitted(ApplicationSubmittedEvent event) {
        try {
            handleApplicationSubmitted(event);
        } catch (Exception e) {
            log.error("🚫 Failed to process ApplicationSubmittedEvent: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "application-lifecycle", groupId = "realtime-group-verification-started")
    public void listenVerificationStarted(VerificationStartedEvent event) {
        try {
            handleVerificationStarted(event);
        } catch (Exception e) {
            log.error("🚫 Failed to process VerificationStartedEvent: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "application-lifecycle", groupId = "realtime-group-app-under-review")
    public void listenApplicationUnderReview(ApplicationUnderReviewEvent event) {
        try {
            handleApplicationUnderReview(event);
        } catch (Exception e) {
            log.error("🚫 Failed to process ApplicationUnderReviewEvent: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "application-lifecycle", groupId = "realtime-group-verification-completed")
    public void listenVerificationCompleted(VerificationCompletedEvent event) {
        try {
            handleVerificationCompleted(event);
        } catch (Exception e) {
            log.error("🚫 Failed to process VerificationCompletedEvent: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "application-lifecycle", groupId = "realtime-group-app-sent-to-pcic")
    public void listenApplicationSentToPcic(ApplicationSentToPcicEvent event) {
        try {
            handleApplicationSentToPcic(event);
        } catch (Exception e) {
            log.error("🚫 Failed to process ApplicationSentToPcicEvent: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "application-lifecycle", groupId = "realtime-group-app-received-by-pcic")
    public void listenApplicationReceivedByPcic(ApplicationReceivedByPcicEvent event) {
        try {
            handleApplicationReceivedByPcic(event);
        } catch (Exception e) {
            log.error("🚫 Failed to process ApplicationReceivedByPcicEvent: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "application-lifecycle", groupId = "realtime-group-inspection-scheduled")
    public void listenInspectionScheduled(InspectionScheduledEvent event) {
        try {
            handleInspectionScheduled(event);
        } catch (Exception e) {
            log.error("🚫 Failed to process InspectionScheduledEvent: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "application-lifecycle", groupId = "realtime-group-inspection-completed")
    public void listenInspectionCompleted(InspectionCompletedEvent event) {
        try {
            handleInspectionCompleted(event);
        } catch (Exception e) {
            log.error("🚫 Failed to process InspectionCompletedEvent: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "application-lifecycle", groupId = "realtime-group-policy-issued")
    public void listenPolicyIssued(PolicyIssuedEvent event) {
        try {
            handlePolicyIssued(event);
        } catch (Exception e) {
            log.error("🚫 Failed to process PolicyIssuedEvent: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "application-lifecycle", groupId = "realtime-group-claim-processed")
    public void listenClaimProcessed(ClaimProcessedEvent event) {
        try {
            handleClaimProcessed(event);
        } catch (Exception e) {
            log.error("🚫 Failed to process ClaimProcessedEvent: {}", e.getMessage(), e);
        }
    }

    private void handleApplicationSubmitted(ApplicationSubmittedEvent e) {
        handleNotification(
            e.getUserId(),
            ApplicationNotificationDTO.builder()
                .id(e.getSubmissionId())
                .title("Application Submitted")
                .message("Your application has been submitted successfully.")
                .time(e.getSubmittedAt() != null ? e.getSubmittedAt() : LocalDateTime.now())
                .read(false)
                .build(),
            "Application Received",
            "Your application has been submitted successfully."
        );
    }

    private void handleVerificationStarted(VerificationStartedEvent e) {
        handleNotification(
            e.getUserId(),
            ApplicationNotificationDTO.builder()
                .id(e.getSubmissionId())
                .title("Verification Started")
                .message("Verification process has started for your application.")
                .time(e.getStartedAt() != null ? e.getStartedAt() : LocalDateTime.now())
                .read(false)
                .build(),
            "Verification Started",
            "Verification process has started for your application."
        );
    }

    private void handleApplicationUnderReview(ApplicationUnderReviewEvent e) {
        handleNotification(
            e.getUserId(),
            ApplicationNotificationDTO.builder()
                .id(e.getSubmissionId())
                .title("Application Under Review")
                .message("Your application is now under review.")
                .time(e.getReviewStartedAt() != null ? e.getReviewStartedAt() : LocalDateTime.now())
                .read(false)
                .build(),
            "Application Under Review",
            "Your application is now under review."
        );
    }

    private void handleVerificationCompleted(VerificationCompletedEvent e) {
        handleNotification(
            e.getUserId(),
            ApplicationNotificationDTO.builder()
                .id(e.getSubmissionId())
                .title("Verification Completed")
                .message("Verification completed: " + e.getStatus())
                .time(e.getVerifiedAt() != null ? e.getVerifiedAt() : LocalDateTime.now())
                .read(false)
                .build(),
            "Verification " + e.getStatus(),
            "Verification completed: " + e.getStatus()
        );
    }

    private void handleApplicationSentToPcic(ApplicationSentToPcicEvent e) {
        handleNotification(
            e.getUserId(),
            ApplicationNotificationDTO.builder()
                .id(e.getSubmissionId())
                .title("Application Sent to PCIC")
                .message("Your application has been sent to PCIC.")
                .time(e.getSentAt() != null ? e.getSentAt() : LocalDateTime.now())
                .read(false)
                .build(),
            "Application Sent to PCIC",
            "Your application has been sent to PCIC."
        );
    }

    private void handleApplicationReceivedByPcic(ApplicationReceivedByPcicEvent e) {
        handleNotification(
            e.getUserId(),
            ApplicationNotificationDTO.builder()
                .id(e.getSubmissionId())
                .title("Application Received by PCIC")
                .message("PCIC has received your application. Status: " + e.getVerificationStatus())
                .time(e.getReceivedAt() != null ? e.getReceivedAt() : LocalDateTime.now())
                .read(false)
                .build(),
            "Application Received by PCIC",
            "PCIC has received your application. Status: " + e.getVerificationStatus()
        );
    }

    private void handleInspectionScheduled(InspectionScheduledEvent e) {
        handleNotification(
            e.getUserId(),
            ApplicationNotificationDTO.builder()
                .id(e.getSubmissionId())
                .title("Inspection Scheduled")
                .message("An inspection has been scheduled for your application.")
                .time(e.getScheduledAt() != null ? e.getScheduledAt() : LocalDateTime.now())
                .read(false)
                .build(),
            "Inspection Scheduled",
            "An inspection has been scheduled for your application."
        );
    }

    private void handleInspectionCompleted(InspectionCompletedEvent e) {
        handleNotification(
            e.getUserId(),
            ApplicationNotificationDTO.builder()
                .id(e.getSubmissionId())
                .title("Inspection Completed")
                .message("Inspection for your application has been completed.")
                .time(e.getInspectedAt() != null ? e.getInspectedAt() : LocalDateTime.now())
                .read(false)
                .build(),
            "Inspection Completed",
            "Inspection for your application has been completed."
        );
    }

    private void handlePolicyIssued(PolicyIssuedEvent e) {
        handleNotification(
            e.getUserId(),
            ApplicationNotificationDTO.builder()
                .id(e.getSubmissionId())
                .title("Policy Issued")
                .message("A policy has been issued for your application. Policy #: " + e.getPolicyNumber())
                .time(e.getIssuedAt() != null ? e.getIssuedAt() : LocalDateTime.now())
                .read(false)
                .build(),
            "Policy Issued: #" + e.getPolicyNumber(),
            "A policy has been issued for your application. Policy #: " + e.getPolicyNumber()
        );
    }

    private void handleClaimProcessed(ClaimProcessedEvent e) {
        handleNotification(
            e.getUserId(),
            ApplicationNotificationDTO.builder()
                .id(e.getSubmissionId())
                .title("Claim Processed")
                .message("Your claim has been processed. Status: " + e.getPayoutStatus())
                .time(e.getProcessedAt() != null ? e.getProcessedAt() : LocalDateTime.now())
                .read(false)
                .build(),
            "Claim Processed",
            "Your claim has been processed. Status: " + e.getPayoutStatus()
        );
    }

    private void handleNotification(UUID receiverId, ApplicationNotificationDTO notification, String emailSubject, String emailMessage) {
        if (receiverId == null || notification == null) {
            log.warn("Could not extract receiverId or notification from event");
            return;
        }
        Notification notificationEntity = Notification.builder()
                .recipient(receiverId.toString())
                .type("APPLICATION")
                .status("SENT")
                .title(notification.getTitle())
                .message(notification.getMessage())
                .createdAt(notification.getTime() != null ? notification.getTime() : LocalDateTime.now())
                .build();
        log.info("✅ Saved notification to database for user {}", receiverId);
        notificationRepository.save(notificationEntity);
        messagingTemplate.convertAndSendToUser(
                receiverId.toString(),
                "/queue/application.notifications",
                notification
        );
        log.info("✅ Sent WebSocket notification to user {}", receiverId);
        try {
            FarmerResponse farmer = farmerServiceClient.getFarmerById(receiverId);
            String recipientEmail = farmer.getEmail();
            if (recipientEmail != null && !recipientEmail.isBlank()) {
                emailService.sendEmail(recipientEmail, emailSubject, emailMessage, false);
                log.info("✅ Sent email notification to {}", recipientEmail);
            } else {
                log.warn("No email found for userId={}", receiverId);
            }
        } catch (Exception ex) {
            log.error("🚫 Failed to send email notification: {}", ex.getMessage(), ex);
        }
    }
}
