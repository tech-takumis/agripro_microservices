package com.hashjosh.notification.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.hashjosh.kafkacommon.agriculture.BatchCreatedEvent;
import com.hashjosh.notification.clients.FarmerResponse;
import com.hashjosh.notification.clients.FarmerServiceClient;
import com.hashjosh.notification.entity.Notification;
import com.hashjosh.notification.repository.NotificationRepository;
import com.hashjosh.notification.utils.NotificationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AgricultureNotificationService {

    private final TemplateEngine templateEngine;
    private final NotificationRepository notificationRepository;
    private final FarmerServiceClient farmerServiceClient;
    private final EmailService emailService;
    public void sendBatchCreatedNotification(BatchCreatedEvent event) {
        List<FarmerResponse> farmers = farmerServiceClient.getAllFarmers();
        String subject = "New Batch Created: " + event.getName();

        farmers.forEach(FarmerResponse -> {
            System.out.println("Farmer Email: " + FarmerResponse.getEmail());
            String recipientEmail = FarmerResponse.getEmail();
            Context context = new Context();
            context.setVariable("email", recipientEmail);
            context.setVariable("batchName", event.getName());
            context.setVariable("batchStatus", event.getStatus());
            context.setVariable("startDate", event.getStartDate());
            context.setVariable("endDate", event.getEndDate());
            String emailContent = templateEngine.process("email/batch-created", context);

            // Create and save notification
            Notification<JsonNode> notification = NotificationUtils.createEmailNotification(
                    recipientEmail,
                    subject,
                    emailContent,
                    null
            );


            emailService.sendEmail(recipientEmail,subject,emailContent, true);
            // Update notification status to SENT
            notification.setStatus("SENT");

            notificationRepository.save(notification);

        });
    }
}
