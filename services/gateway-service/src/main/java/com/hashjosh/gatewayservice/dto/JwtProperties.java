package com.hashjosh.gatewayservice.dto;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(
        String secret,
        String cookieName,
        String expiration
) {
}
