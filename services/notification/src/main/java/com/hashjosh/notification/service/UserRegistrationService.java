package com.hashjosh.notification.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hashjosh.kafkacommon.user.UserRegistrationContract;
import com.hashjosh.notification.entity.Notification;
import com.hashjosh.notification.repository.NotificationRepository;
import com.hashjosh.notification.utils.NotificationUtils;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.util.Base64;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserRegistrationService {

    private final TemplateEngine templateEngine;
    private final NotificationRepository notificationRepository;
    private final JavaMailSender mailSender;

    public void sendUserRegistrationEmailNotification(UserRegistrationContract contract) {
        try {
            // Prepare email content
            String subject = "Welcome to Our Platform - Your Account Details";
            String recipientEmail = contract.getEmail();

            // Decode the password (assuming it's Base64 encoded)
            String decodedPassword = new String(Base64.getDecoder().decode(contract.getPassword()));

            // Create email content using Thymeleaf template
            Context context = new Context();
            context.setVariable("firstName", contract.getFirstName());
            context.setVariable("lastName", contract.getLastName());
            context.setVariable("username", contract.getUsername());
            context.setVariable("password", decodedPassword);
            context.setVariable("email", contract.getEmail());
            context.setVariable("registrationDate", LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));

            String emailContent = templateEngine.process("email/user-registration", context);

            // Create and save notification
            Notification<JsonNode> notification = NotificationUtils.createEmailNotification(
                    recipientEmail,
                    subject,
                    emailContent,
                    null
            );

            // Send email
            sendEmail(recipientEmail, subject, emailContent, true);

            // Update notification status to SENT
            notification.setStatus("SENT");
            notificationRepository.save(notification);

            log.info("✅ Registration email with credentials sent to: {}", recipientEmail);

        } catch (Exception e) {
            log.error("❌ Failed to send registration email notification to: {}", contract.getEmail(), e);
            saveFailedNotification(contract, "Failed to send registration email: " + e.getMessage());
            throw new RuntimeException("Failed to send registration email notification", e);
        }
    }


    private void sendEmail(String to, String subject, String content, boolean isHtml) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, isHtml);

            mailSender.send(message);
        } catch (MessagingException e) {
            log.error("❌ Error sending email to {}: {}", to, e.getMessage());
            throw new RuntimeException("Failed to send email", e);
        }
    }

    private void saveFailedNotification(UserRegistrationContract contract, String errorMessage) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Notification<JsonNode> failedNotification = NotificationUtils.createNotification(
                    contract.getEmail(),
                    "EMAIL",
                    "Failed: User Registration Notification",
                    objectMapper.createObjectNode()
                            .put("error", errorMessage)
                            .put("userId", contract.getUserId().toString())
            );
            failedNotification.setStatus("FAILED");
            notificationRepository.save(failedNotification);
        } catch (Exception e) {
            log.error("❌ Failed to save failed notification for user: {}", contract.getEmail(), e);
        }
    }
}