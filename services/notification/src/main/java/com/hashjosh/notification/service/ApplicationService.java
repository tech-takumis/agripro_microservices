package com.hashjosh.notification.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hashjosh.kafkacommon.application.ApplicationSubmissionContract;
import com.hashjosh.notification.dto.EmailNotificationPayload;
import com.hashjosh.notification.entity.Notification;
import com.hashjosh.notification.properties.EmailProperties;
import com.hashjosh.notification.repository.NotificationRepository;
import com.hashjosh.notification.utils.NotificationUtils;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.UnsupportedEncodingException;
import java.time.format.DateTimeFormatter;

@Service
@Slf4j
@RequiredArgsConstructor
public class ApplicationService {

    private final TemplateEngine templateEngine;
    private final NotificationRepository notificationRepository;
    private final JavaMailSender mailSender;
    private final ObjectMapper objectMapper;
    private final EmailProperties emailProperties;

    public void sendEmailNotification(ApplicationSubmissionContract applicationSubmissionContract) {
        try {
            // Prepare email content
            String subject = "Application Submitted Successfully";


            String recipientEmail = applicationSubmissionContract.getGmail();

            // Create email content using Thymeleaf template
            Context context = new Context();
            context.setVariable("applicationId", applicationSubmissionContract.getApplicationId());
            context.setVariable("submissionDate", applicationSubmissionContract.getOccurredAt()
                    .format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm")));

            String emailContent = templateEngine.process("email/application-submitted", context);


            EmailNotificationPayload emailPayload = new EmailNotificationPayload(
                    subject,
                    emailContent,
                    null // No attachment for now
            );

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

            log.info("✅ Email notification sent for application ID: {}", applicationSubmissionContract.getApplicationId());

        } catch (Exception e) {
            log.error("❌ Failed to send email notification for application ID: {}",
                    applicationSubmissionContract.getApplicationId(), e);
            // Save failed notification
            saveFailedNotification(applicationSubmissionContract, "Failed to send email: " + e.getMessage());
            throw new RuntimeException("Failed to send email notification", e);
        }
    }

    private void sendEmail(String to, String subject, String content, boolean isHtml) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, isHtml);

            helper.setFrom(emailProperties.from(), emailProperties.senderName());

            mailSender.send(message);
        } catch (MessagingException e) {
            log.error("❌ Error sending email to {}: {}", to, e.getMessage());
            throw new RuntimeException("Failed to send email", e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    private void saveFailedNotification(ApplicationSubmissionContract contract, String errorMessage) {
        try {
            Notification<JsonNode> failedNotification = NotificationUtils.createNotification(
                    contract.getUploadedBy().toString(),
                    "EMAIL",
                    "Failed: Application Submission Notification",
                    objectMapper.createObjectNode()
                            .put("error", errorMessage)
                            .put("applicationId", contract.getApplicationId().toString())
            );
            failedNotification.setStatus("FAILED");
            notificationRepository.save(failedNotification);
        } catch (Exception e) {
            log.error("❌ Failed to save failed notification for application ID: {}",
                    contract.getApplicationId(), e);
        }
    }
}
