package com.hashjosh.notification.service;

import com.hashjosh.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;

@Service
@RequiredArgsConstructor
@Slf4j
public class FarmerRegistrationNotificationService {

    private final TemplateEngine templateEngine;
    private final NotificationRepository notificationRepository;
    private final EmailService emailService;


}
