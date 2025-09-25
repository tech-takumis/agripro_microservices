package com.hashjosh.notification.kafka;

import com.hashjosh.notification.service.UserRegistrationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserRegistrationConsumer {

    private UserRegistrationService userRegistrationService;
}
