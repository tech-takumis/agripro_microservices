package com.hashjosh.rsbsa;

import com.hashjosh.jwtshareable.config.JwtConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({JwtConfig.class})
public class RsbsaApplication {

    public static void main(String[] args) {
        SpringApplication.run(RsbsaApplication.class, args);
    }

}
