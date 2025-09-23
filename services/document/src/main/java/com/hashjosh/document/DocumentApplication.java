package com.hashjosh.document;

import com.hashjosh.document.properties.MinioProperties;
import com.hashjosh.jwtshareable.config.JwtConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@Import({JwtConfig.class})
@EnableConfigurationProperties({MinioProperties.class})
@EnableWebMvc
public class DocumentApplication {

	public static void main(String[] args) {
		SpringApplication.run(DocumentApplication.class, args);
	}

}
