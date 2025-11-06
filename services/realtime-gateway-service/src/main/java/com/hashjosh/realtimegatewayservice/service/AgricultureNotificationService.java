package com.hashjosh.realtimegatewayservice.service;

import com.hashjosh.kafkacommon.agriculture.AgricultureRegistrationContract;
import com.hashjosh.kafkacommon.agriculture.NewInvitationEvent;
import com.hashjosh.realtimegatewayservice.entity.Notification;
import com.hashjosh.realtimegatewayservice.exception.ApiException;
import com.hashjosh.realtimegatewayservice.repository.NotificationRepository;
import com.hashjosh.realtimegatewayservice.utils.NotificationUtils;
import com.sun.jdi.event.ExceptionEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class AgricultureNotificationService {

    private final TemplateEngine templateEngine;
    private final EmailService emailService;
    private final NotificationRepository notificationRepository;

    @Value("${frontend[0].da_url}")
    private static final String DA_URL = "http://localhost:5173";

    @KafkaListener(topics = "agriculture-registration", groupId = "notification-group" )
    public void sendAgricultureRegistrationEmailNotification(@Payload AgricultureRegistrationContract event) {
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

    @KafkaListener(topics = "agriculture-invitations", groupId = "new-invitation" )
    public void sendAgricultureInvitationEvent(@Payload NewInvitationEvent event){
        try{
            String subject = "Invitation to join the platform";
            String registrationLink = "http://localhost:5174/register?token=" + event.getToken();
            String recipientEmail = event.getEmail();

            //Create the context using thymeleaf
            Context context = new Context();
            context.setVariable("registrationLink", registrationLink);
            context.setVariable("expiryDate", event.getExpiryDate()
                    .format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm")));
            String emailContent = templateEngine.process("email/agriculture-invitation", context);
            //Create and save notification
            Notification notification = NotificationUtils.createEmailNotification(
                    recipientEmail,
                    subject,
                    "Invitation to join the platform"
            );
            //Send email
            emailService.sendEmail(recipientEmail, subject, emailContent, true);
            //Update notification status to SENT
            notification.setStatus("SENT");
            notificationRepository.save(notification);
            log.info("✅ Invitation email sent to: {}", recipientEmail);
        }catch (Exception e){
            log.error("❌ Failed to send invitation email notification to: {}", event.getEmail(), e);
            throw ApiException.badRequest("Failed to send invitation email notification");
        }
    }
}
