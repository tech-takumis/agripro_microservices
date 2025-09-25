package com.hashjosh.users;

import com.hashjosh.jwtshareable.config.JwtConfig;
import com.hashjosh.jwtshareable.utils.SlugUtil;
import com.hashjosh.users.properties.TenantProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableAsync
@EnableConfigurationProperties({ TenantProperties.class})
@Import({JwtConfig.class, SlugUtil.class})
@EnableTransactionManagement
@EnableKafka
public class UserApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class, args);
    }

}
