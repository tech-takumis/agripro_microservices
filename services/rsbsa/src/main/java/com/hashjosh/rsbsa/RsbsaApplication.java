package com.hashjosh.rsbsa;

import com.hashjosh.jwtshareable.config.JwtConfig;
import com.hashjosh.jwtshareable.properties.JwtProperties;
import com.hashjosh.rsbsa.config.TrustedConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@EnableConfigurationProperties({JwtProperties.class, TrustedConfig.class})
@Import({JwtConfig.class})
@EnableDiscoveryClient
public class RsbsaApplication {

    public static void main(String[] args) {
        SpringApplication.run(RsbsaApplication.class, args);
    }

}
