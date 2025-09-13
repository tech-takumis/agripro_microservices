package com.hashjosh.jwtshareable;

import com.hashjosh.jwtshareable.properties.JwtProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({JwtProperties.class})
public class JwtshareableApplication {

    public static void main(String[] args) {
        SpringApplication.run(JwtshareableApplication.class, args);
    }

}
