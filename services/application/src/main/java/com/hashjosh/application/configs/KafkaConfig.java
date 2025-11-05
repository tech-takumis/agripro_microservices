package com.hashjosh.application.configs;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic applicationSubmitted(){
        return TopicBuilder
                .name("application-submitted")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic applicationForwarded(){
        return TopicBuilder
                .name("application-forwarded")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic applicationReceived(){
        return TopicBuilder
                .name("application-received")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic applicationVerified(){
        return TopicBuilder
                .name("application-verified")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic applicationUnderReview(){
        return TopicBuilder
                .name("application-underReview")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic applicationInspectionSchedule(){
        return TopicBuilder
                .name("application-inspection-schedule")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic applicationInspectionCompleted(){
        return TopicBuilder
                .name("application-inspection-completed")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic applicationPolicyIssued(){
        return TopicBuilder
                .name("application-policy-issued")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic applicationClaim(){
        return TopicBuilder
                .name("application-claim")
                .partitions(1)
                .replicas(1)
                .build();
    }

}
