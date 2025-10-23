package com.hashjosh.realtimegatewayservice.service;

import com.hashjosh.realtimegatewayservice.exception.ApiException;
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

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
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
            throw ApiException.badRequest("Failed to send email");
        }
    }
}
