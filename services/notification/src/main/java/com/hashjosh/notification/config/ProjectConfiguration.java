package com.hashjosh.notification.config;

import com.hashjosh.notification.properties.EmailProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import java.util.Properties;

@Configuration
@EnableConfigurationProperties(EmailProperties.class)
public class ProjectConfiguration {

    @Bean
    public JavaMailSender javaMailSender(EmailProperties emailProperties) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(emailProperties.host());
        mailSender.setPort(emailProperties.port());
        mailSender.setUsername(emailProperties.username());
        mailSender.setPassword(emailProperties.password());

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", String.valueOf(emailProperties.smtp().auth()));
        props.put("mail.smtp.starttls.enable", String.valueOf(emailProperties.smtp().starttlsEnable()));
        props.put("mail.smtp.starttls.required", String.valueOf(emailProperties.smtp().starttlsRequired()));
        props.put("mail.smtp.connectiontimeout", String.valueOf(emailProperties.smtp().connectionTimeout()));
        props.put("mail.smtp.timeout", String.valueOf(emailProperties.smtp().timeout()));
        props.put("mail.smtp.writetimeout", String.valueOf(emailProperties.smtp().writeTimeout()));
        props.put("mail.debug", "true");
        props.put("mail.smtp.ssl.trust", emailProperties.host());

        return mailSender;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}