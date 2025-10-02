package com.hashjosh.pcic;

import com.hashjosh.jwtshareable.properties.JwtProperties;
import com.hashjosh.jwtshareable.service.JwtService;
import com.hashjosh.jwtshareable.utils.SlugUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableDiscoveryClient
@EnableConfigurationProperties({JwtProperties.class})
@EnableKafka
@Import({JwtService.class, SlugUtil.class})
public class PcicApplication {

	public static void main(String[] args) {
		SpringApplication.run(PcicApplication.class, args);
	}

}
