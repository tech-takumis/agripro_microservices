package com.hashjosh.realtimegatewayservice.service;

import com.hashjosh.kafkacommon.agriculture.AgricultureRegistrationContract;
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
public class AgricultureRegistrationNotificationService {

    private final TemplateEngine templateEngine;
    private final EmailService emailService;
    private final NotificationRepository notificationRepository;

    @KafkaListener(topics = "agriculture-events", groupId = "notification-group" )
    public void sendAgricultureRegistrationEmailNotification(AgricultureRegistrationContract event) {
        try {
            // Prepare email content
            String subject = "Welcome to Our Platform - Your Account Details";
            String recipientEmail = event.getEmail();

            // Create email content using Thymeleaf template
            Context context = new Context();
            context.setVariable("firstName", event.getFirstName());
            context.setVariable("lastName", event.getLastName());
            context.setVariable("username", event.getUsername());
            context.setVariable("password", event.getPassword());
            context.setVariable("email", event.getEmail());
            context.setVariable("registrationDate", LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));

            String emailContent = templateEngine.process("email/agriculture-registration", context);

            // Create and save notification
            Notification notification = NotificationUtils.createEmailNotification(
                    event.getUsername(),
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
            log.error("❌ Failed to send registration email notification to: {}", event.getEmail(), e);
            throw ApiException.badRequest("Failed to send registration email notification");
        }
    }


}
