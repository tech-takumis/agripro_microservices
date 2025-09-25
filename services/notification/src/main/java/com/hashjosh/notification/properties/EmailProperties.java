package com.hashjosh.notification.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "notification.email")
public record EmailProperties(
    String from,
    String senderName,
    String host,
    int port,
    String username,
    String password,
    SmtpProperties smtp
) {
    public record SmtpProperties(
        boolean auth,
        boolean starttlsEnable,
        boolean starttlsRequired,
        int connectionTimeout,
        int timeout,
        int writeTimeout
    ) {}
}