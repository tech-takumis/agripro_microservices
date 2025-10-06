package com.hashjosh.communication;

import com.hashjosh.jwtshareable.config.JwtConfig;
import com.hashjosh.jwtshareable.properties.JwtProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Import;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

@SpringBootApplication
@Import({JwtConfig.class})
@EnableConfigurationProperties({JwtProperties.class})
@EnableWebSocket
@EnableDiscoveryClient
public class CommunicationApplication {

	public static void main(String[] args) {
		SpringApplication.run(CommunicationApplication.class, args);
	}

}
