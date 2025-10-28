package com.hashjosh.realtimegatewayservice.kafka;

import com.hashjosh.kafkacommon.application.*;
import com.hashjosh.realtimegatewayservice.clients.FarmerResponse;
import com.hashjosh.realtimegatewayservice.clients.FarmerServiceClient;
import com.hashjosh.realtimegatewayservice.entity.Notification;
import com.hashjosh.realtimegatewayservice.repository.NotificationRepository;
import com.hashjosh.realtimegatewayservice.service.EmailService;
import com.hashjosh.realtimegatewayservice.dto.NotificationResponseDTO;
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
    private final NotificationRepository notificationRepository;

    @KafkaListener(topics = "application-lifecycle", groupId = "realtime-group-app-submitted")
    public void listenApplicationSubmitted(@Payload ApplicationSubmittedEvent event) {
        handleApplicationSubmitted(event);
    }

    @KafkaListener(topics = "application-lifecycle", groupId = "realtime-group-verification-started")
    public void listenVerificationStarted(@Payload VerificationStartedEvent event) {
        handleVerificationStarted(event);
    }

    @KafkaListener(topics = "application-lifecycle", groupId = "realtime-group-verification-completed")
    public void listenVerificationCompleted(@Payload VerificationCompletedEvent event) {
        handleVerificationCompleted(event);
    }

    @KafkaListener(topics = "application-lifecycle", groupId = "realtime-group-app-under-review")
    public void listenApplicationUnderReview(@Payload ApplicationUnderReviewEvent event) {
        handleApplicationUnderReview(event);
    }

    @KafkaListener(topics = "application-lifecycle", groupId = "realtime-group-app-forwarded")
    public void listenApplicationForwarded(@Payload ApplicationForwarded event) {
        handleApplicationForwarded(event);
    }

    @KafkaListener(topics = "application-lifecycle", groupId = "realtime-group-app-received")
    public void listenApplicationReceived(@Payload ApplicationReceived event) {
        handleApplicationReceived(event);
    }

    @KafkaListener(topics = "application-lifecycle", groupId = "realtime-group-inspection-scheduled")
    public void listenInspectionScheduled(@Payload InspectionScheduledEvent event) {
        handleInspectionScheduled(event);
    }

    @KafkaListener(topics = "application-lifecycle", groupId = "realtime-group-inspection-completed")
    public void listenInspectionCompleted(@Payload InspectionCompletedEvent event) {
        handleInspectionCompleted(event);
    }

    @KafkaListener(topics = "application-lifecycle", groupId = "realtime-group-policy-issued")
    public void listenPolicyIssued(@Payload PolicyIssuedEvent event) {
        handlePolicyIssued(event);
    }

    @KafkaListener(topics = "application-lifecycle", groupId = "realtime-group-claim-processed")
    public void listenClaimProcessed(@Payload ClaimProcessedEvent event) {
        handleClaimProcessed(event);
    }

    private void handleApplicationSubmitted(ApplicationSubmittedEvent e) {
        handleNotification(
            e.getUserId(),
            NotificationResponseDTO.builder()
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

    private void handleApplicationForwarded(ApplicationForwarded e) {
        handleNotification(
                e.getUserId(),
                NotificationResponseDTO.builder()
                        .id(e.getSubmissionId())
                        .title("Application Sent to "+ e.getProvider())
                        .message("Your application has been sent to "+ e.getProvider())
                        .time(e.getSentAt() != null ? e.getSentAt() : LocalDateTime.now())
                        .read(false)
                        .build(),
                "Application Sent to "+ e.getProvider(),
                "Your application has been sent to "+ e.getProvider()
        );
    }

    private void handleVerificationStarted(VerificationStartedEvent e) {
        handleNotification(
            e.getUserId(),
            NotificationResponseDTO.builder()
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

    private void handleVerificationCompleted(VerificationCompletedEvent e) {
        handleNotification(
                e.getUserId(),
                NotificationResponseDTO.builder()
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

    private void handleApplicationUnderReview(ApplicationUnderReviewEvent e) {
        handleNotification(
            e.getUserId(),
            NotificationResponseDTO.builder()
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

    private void handleApplicationReceived(ApplicationReceived e) {
        handleNotification(
            e.getUserId(),
            NotificationResponseDTO.builder()
                .id(e.getSubmissionId())
                .title("Application Received by "+e.getProvider())
                .message(e.getProvider()+ " has received your application. Status: " + e.getStatus())
                .time(e.getReceivedAt() != null ? e.getReceivedAt() : LocalDateTime.now())
                .read(false)
                .build(),
            "Application Received by "+e.getProvider(),
            e.getProvider()+ " has received your application. Status: " + e.getStatus()
        );
    }

    private void handleInspectionScheduled(InspectionScheduledEvent e) {
        handleNotification(
            e.getUserId(),
            NotificationResponseDTO.builder()
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
            NotificationResponseDTO.builder()
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
            NotificationResponseDTO.builder()
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
            NotificationResponseDTO.builder()
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

    private void handleNotification(UUID receiverId, NotificationResponseDTO notification, String emailSubject, String emailMessage) {
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
