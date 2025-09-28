package com.hashjosh.rsbsa;

import com.hashjosh.jwtshareable.config.JwtConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({JwtConfig.class})
@EnableDiscoveryClient
public class RsbsaApplication {

    public static void main(String[] args) {
        SpringApplication.run(RsbsaApplication.class, args);
    }

}
