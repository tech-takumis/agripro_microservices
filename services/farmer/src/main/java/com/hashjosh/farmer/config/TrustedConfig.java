package com.hashjosh.farmer.config;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "trusted")
@Getter
@Setter
public class TrustedConfig {
    private List<String> internalServiceIds;
}