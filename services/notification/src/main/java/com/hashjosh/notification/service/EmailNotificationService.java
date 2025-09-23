package com.hashjosh.notification.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hashjosh.kafkacommon.application.ApplicationSubmissionContract;
import com.hashjosh.notification.clients.UserResponse;
import com.hashjosh.notification.clients.UserServiceClient;
import com.hashjosh.notification.dto.EmailNotificationPayload;
import com.hashjosh.notification.entity.Notification;
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

import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailNotificationService {

    private final UserServiceClient userServiceClient;
    private final TemplateEngine templateEngine;
    private final NotificationRepository notificationRepository;
    private final JavaMailSender mailSender;
    private final ObjectMapper objectMapper;

    public void sendEmailNotification(ApplicationSubmissionContract applicationSubmissionContract) {
        try {
            // Prepare email content
            String subject = "Application Submitted Successfully";
            /** We need to get the user here since we pass the token in the contract we will  gonna use
             * that to the the user using the uploaded by field also */
            UserResponse user = userServiceClient.getUserById(
                    applicationSubmissionContract.getUploadedBy(),
                    applicationSubmissionContract.getToken()
            );
            String recipientEmail = user.getEmail();

            // Create email content using Thymeleaf template
            Context context = new Context();
            context.setVariable("applicationId", applicationSubmissionContract.getApplicationId());
            context.setVariable("submissionDate", applicationSubmissionContract.getOccurredAt()
                    .format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm")));

            String emailContent = templateEngine.process("email/application-submitted", context);

            // Create email notification payload
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

            // Uncomment and configure if you need to set a from address
            // helper.setFrom("noreply@yourdomain.com");

            mailSender.send(message);
        } catch (MessagingException e) {
            log.error("❌ Error sending email to {}: {}", to, e.getMessage());
            throw new RuntimeException("Failed to send email", e);
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
