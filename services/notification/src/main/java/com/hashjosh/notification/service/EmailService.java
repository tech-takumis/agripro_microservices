package com.hashjosh.notification.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hashjosh.notification.entity.Notification;
import com.hashjosh.notification.repository.NotificationRepository;
import com.hashjosh.notification.utils.NotificationUtils;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final NotificationRepository notificationRepository;

    @Retryable(
            retryFor = { MessagingException.class, MailSendException.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000)
    )

    public void sendEmail(String to, String subject, String content, boolean isHtml) {
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

    public void saveFailedNotification(String email, String title, UUID userId, String errorMessage) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Notification<JsonNode> failedNotification = NotificationUtils.createNotification(
                    email,
                    "EMAIL",
                    title,
                    objectMapper.createObjectNode()
                            .put("error", errorMessage)
                            .put("userId", userId.toString())
            );
            failedNotification.setStatus("FAILED");
            notificationRepository.save(failedNotification);
        } catch (Exception e) {
            log.error("❌ Failed to save failed notification for user: {}", email, e);
        }
    }
}
