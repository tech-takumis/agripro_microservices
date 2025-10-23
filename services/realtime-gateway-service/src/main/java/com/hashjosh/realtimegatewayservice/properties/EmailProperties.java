package com.hashjosh.realtimegatewayservice.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "notification.email")
public record EmailProperties(
    String from,
    String senderName
    )
{}