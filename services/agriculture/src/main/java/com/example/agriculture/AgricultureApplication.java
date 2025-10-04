package com.example.agriculture;

import com.example.agriculture.config.TrustedConfig;
import com.hashjosh.jwtshareable.properties.JwtProperties;
import com.hashjosh.jwtshareable.service.JwtService;
import com.hashjosh.jwtshareable.utils.SlugUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@EnableDiscoveryClient
@EnableConfigurationProperties({JwtProperties.class, TrustedConfig.class})
@Import({JwtService.class, SlugUtil.class})
public class AgricultureApplication {

    public static void main(String[] args) {
        SpringApplication.run(AgricultureApplication.class, args);
    }

}
