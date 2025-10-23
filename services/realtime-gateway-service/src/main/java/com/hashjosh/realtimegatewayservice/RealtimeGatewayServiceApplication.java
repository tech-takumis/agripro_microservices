package com.hashjosh.realtimegatewayservice;

import com.hashjosh.jwtshareable.config.JwtConfig;
import com.hashjosh.jwtshareable.properties.JwtProperties;
import com.hashjosh.jwtshareable.service.JwtService;
import com.hashjosh.realtimegatewayservice.config.TrustedService;
import com.hashjosh.realtimegatewayservice.properties.EmailProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

@SpringBootApplication
@EnableConfigurationProperties({JwtProperties.class, TrustedService.class, EmailProperties.class})
@Import({JwtConfig.class})
@EnableKafka
@EnableWebSocket
@EnableDiscoveryClient
public class RealtimeGatewayServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(RealtimeGatewayServiceApplication.class, args);
    }

}
