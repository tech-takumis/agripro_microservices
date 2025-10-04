package com.hashjosh.application;


import com.hashjosh.application.configs.TrustedConfig;
import com.hashjosh.jwtshareable.properties.JwtProperties;
import com.hashjosh.jwtshareable.service.JwtService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableConfigurationProperties({JwtProperties.class, TrustedConfig.class})
@EnableKafka
@EnableDiscoveryClient
@Import(JwtService.class)
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
