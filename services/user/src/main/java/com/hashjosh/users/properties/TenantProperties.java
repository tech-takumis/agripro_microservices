package com.hashjosh.users.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Data
@Component
@ConfigurationProperties(prefix = "multitenancy")
public class TenantProperties {
    private String defaultTenant;
    private Map<String, DataSourceProperties> tenants;

    @Data
    public static class DataSourceProperties {
        private String url;
        private String username;
        private String password;
        private String driverClassName;
    }
}

