package com.hashjosh.program;

import com.hashjosh.jwtshareable.properties.JwtProperties;
import com.hashjosh.jwtshareable.service.JwtService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({JwtService.class})
@EnableConfigurationProperties({JwtProperties.class})
public class ProgramApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProgramApplication.class, args);
    }

}
