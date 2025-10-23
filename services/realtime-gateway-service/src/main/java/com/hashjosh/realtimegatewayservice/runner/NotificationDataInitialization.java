package com.hashjosh.realtimegatewayservice.runner;

import com.hashjosh.realtimegatewayservice.entity.Notification;
import com.hashjosh.realtimegatewayservice.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationDataInitialization implements CommandLineRunner {

    private final NotificationRepository notificationRepository;

    @Override
    public void run(String... args) {
        if (notificationRepository.count() > 0) {
            log.info("Notifications already initialized, skipping...");
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        List<Notification> notifications = List.of(
            Notification.builder()
                .recipient("RSBSA-2025-001")
                .type("EMAIL")
                .status("SENT")
                .title("Welcome to the Agriculture Portal")
                .message("Your account has been successfully created. Start exploring the portal now!")
                .createdAt(now.minusDays(10))
                .updatedAt(now.minusDays(10))
                .build(),
            Notification.builder()
                .recipient("RSBSA-2025-001")
                .type("EMAIL")
                .status("SENT")
                .title("Application Submitted")
                .message("Your crop insurance application has been submitted for review.")
                .createdAt(now.minusDays(9))
                .updatedAt(now.minusDays(9))
                .build(),
            Notification.builder()
                .recipient("RSBSA-2025-001")
                .type("EMAIL")
                .status("SENT")
                .title("Application Approved")
                .message("Congratulations! Your crop insurance application has been approved.")
                .createdAt(now.minusDays(8))
                .updatedAt(now.minusDays(8))
                .build(),
            Notification.builder()
                .recipient("RSBSA-2025-001")
                .type("EMAIL")
                .status("SENT")
                .title("Premium Payment Reminder")
                .message("Please pay your insurance premium before the due date to keep your policy active.")
                .createdAt(now.minusDays(7))
                .updatedAt(now.minusDays(7))
                .build(),
            Notification.builder()
                .recipient("RSBSA-2025-001")
                .type("EMAIL")
                .status("SENT")
                .title("Premium Payment Received")
                .message("We have received your premium payment. Thank you!")
                .createdAt(now.minusDays(6))
                .updatedAt(now.minusDays(6))
                .build(),
            Notification.builder()
                .recipient("RSBSA-2025-001")
                .type("EMAIL")
                .status("SENT")
                .title("Policy Issued")
                .message("Your crop insurance policy has been issued. Check your dashboard for details.")
                .createdAt(now.minusDays(5))
                .updatedAt(now.minusDays(5))
                .build(),
            Notification.builder()
                .recipient("RSBSA-2025-001")
                .type("EMAIL")
                .status("SENT")
                .title("Claim Submission Received")
                .message("Your claim has been received and is under review.")
                .createdAt(now.minusDays(4))
                .updatedAt(now.minusDays(4))
                .build(),
            Notification.builder()
                .recipient("RSBSA-2025-001")
                .type("EMAIL")
                .status("SENT")
                .title("Claim Approved")
                .message("Good news! Your claim has been approved. Indemnity will be processed soon.")
                .createdAt(now.minusDays(3))
                .updatedAt(now.minusDays(3))
                .build(),
            Notification.builder()
                .recipient("RSBSA-2025-001")
                .type("EMAIL")
                .status("SENT")
                .title("Indemnity Processed")
                .message("Your indemnity payment has been processed. Please check your account.")
                .createdAt(now.minusDays(2))
                .updatedAt(now.minusDays(2))
                .build(),
            Notification.builder()
                .recipient("RSBSA-2025-001")
                .type("EMAIL")
                .status("SENT")
                .title("Survey Scheduled")
                .message("A field survey has been scheduled for your farm. Please be available on the scheduled date.")
                .createdAt(now.minusDays(1))
                .updatedAt(now.minusDays(1))
                .build()
        );

        notificationRepository.saveAll(notifications);
        log.info("Initialized {} notifications for recipient RSBA-2025-001", notifications.size());
    }
}
