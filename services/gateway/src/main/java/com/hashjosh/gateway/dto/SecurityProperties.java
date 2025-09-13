package com.hashjosh.gateway.dto;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "app.security")
@Getter
@Setter
public class SecurityProperties{

    private List<PublicEndpoint> publicPaths = new ArrayList<>();

    @Getter
    @Setter
    public static class PublicEndpoint {
        private String path;
        private String method;
    }
}
