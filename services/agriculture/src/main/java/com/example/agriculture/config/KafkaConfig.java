package com.example.agriculture.config;

import com.hashjosh.kafkacommon.agriculture.AgricultureRegistrationContract;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic agricultureTopic(){
        return TopicBuilder
                .name("agriculture-registration")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic invitationTopic(){
        return TopicBuilder
                .name("agriculture-invitations")
                .partitions(1)
                .replicas(1)
                .build();
    }

}
