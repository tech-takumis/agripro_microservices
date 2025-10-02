package com.hashjosh.verification;

import com.hashjosh.jwtshareable.config.JwtConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({JwtConfig.class})
@EnableDiscoveryClient
public class VerificationApplication {

	public static void main(String[] args) {
		SpringApplication.run(VerificationApplication.class, args);
	}

}
