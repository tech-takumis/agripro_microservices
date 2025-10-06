package com.hashjosh.communication.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "trusted")
@Data
public class TrustedProperties {
    private List<String> internalServiceIds;
}
