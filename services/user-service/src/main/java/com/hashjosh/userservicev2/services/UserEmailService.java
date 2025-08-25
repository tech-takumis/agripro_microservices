package com.hashjosh.userservicev2.services;

import com.hashjosh.userservicev2.models.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class UserEmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String sendFrom;

    public UserEmailService(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    @Async
    public void sendStaffRegistrationMail(User staff) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        Context context = new Context();

        context.setVariable("staff", staff);
        String html = templateEngine.process("staff-registration", context);

        helper.setFrom(sendFrom);
        helper.setText(html, true);
        helper.setTo(staff.getEmail());
        helper.setSubject(staff.getUsername());
        mailSender.send(message);

    }

    @Async
    public void sendFarmerRegistrationMail(User user) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        Context context = new Context();

        context.setVariable("user", user);
        String registerHtml = templateEngine.process("register", context);

        helper.setTo(user.getEmail());
        helper.setFrom(sendFrom);
        helper.setSubject("Farmer Registration");
        helper.setText(registerHtml, true);

        mailSender.send(message);

    }
}
