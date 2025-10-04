package com.hashjosh.application.configs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Bean
    public ObjectMapper objectMapper(){
        return JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .build();
    }

    @Bean
    public NewTopic applicationTopic(){
        return TopicBuilder
                .name("application-events")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic applicationTopicDlt(){
        return TopicBuilder
                .name("application-events-dlt")
                .partitions(3)
                .replicas(1)
                .build();
    }
}
