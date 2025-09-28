package com.hashjosh.workflow;

import com.hashjosh.jwtshareable.config.JwtConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.annotation.EnableKafka;


@SpringBootApplication
@Import({JwtConfig.class})
@EnableKafka
@EnableDiscoveryClient
public class WorkflowApplication {

	public static void main(String[] args) {
		SpringApplication.run(WorkflowApplication.class, args);
	}

}
