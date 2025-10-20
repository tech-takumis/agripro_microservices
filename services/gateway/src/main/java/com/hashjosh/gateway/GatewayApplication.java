package com.hashjosh.gateway;

import com.hashjosh.gateway.config.TrustedConfig;
import com.hashjosh.gateway.dto.SecurityProperties;
import com.hashjosh.jwtshareable.config.JwtConfig;
import com.hashjosh.jwtshareable.properties.JwtProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@EnableConfigurationProperties({SecurityProperties.class, JwtProperties.class, TrustedConfig.class})
@Import(JwtConfig.class)
@EnableDiscoveryClient
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }

}
