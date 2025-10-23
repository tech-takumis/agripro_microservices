package com.hashjosh.realtimegatewayservice.service;

import com.hashjosh.kafkacommon.farmer.FarmerRegistrationContract;
import com.hashjosh.realtimegatewayservice.entity.Notification;
import com.hashjosh.realtimegatewayservice.exception.ApiException;
import com.hashjosh.realtimegatewayservice.repository.NotificationRepository;
import com.hashjosh.realtimegatewayservice.utils.NotificationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class FarmerRegistrationNotificationService {

    private final TemplateEngine templateEngine;
    private final NotificationRepository notificationRepository;
    private final EmailService emailService;

    @KafkaListener(topics = "farmer-events", groupId = "notification-group")
    public void sendFarmerRegistrationEmailNotification(FarmerRegistrationContract contract) {
        try {
            // Prepare email content
            String subject = "Welcome to Our Platform - Your Account Details";
            String recipientEmail = contract.getEmail();

            // Create email content using Thymeleaf template
            Context context = new Context();
            context.setVariable("firstName", contract.getFirstName());
            context.setVariable("lastName", contract.getLastName());
            context.setVariable("username", contract.getUsername());
            context.setVariable("password", contract.getPassword());
            context.setVariable("email", contract.getEmail());
            context.setVariable("registrationDate", LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));

            String emailContent = templateEngine.process("email/user-registration", context);

            // Create and save notification
            Notification notification = NotificationUtils.createEmailNotification(
                   contract.getUsername(),
                    subject,
                    "Welcome to the platform"
            );

            // Send email
            emailService.sendEmail(recipientEmail, subject, emailContent, true);

            // Update notification status to SENT
            notification.setStatus("SENT");
            notificationRepository.save(notification);

            log.info("✅ Registration email with credentials sent to: {}", recipientEmail);

        } catch (Exception e) {
            log.error("❌ Failed to send registration email notification to: {}", contract.getEmail(), e);
            throw ApiException.badRequest("Failed to send registration email notification");
        }
    }


}
