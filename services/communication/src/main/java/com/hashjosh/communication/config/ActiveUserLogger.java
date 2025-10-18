package com.hashjosh.communication.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ActiveUserLogger {

    private final SimpUserRegistry userRegistry;

    @Scheduled(fixedDelay = 10000)
    public void logActiveUsers() {
        var users = userRegistry.getUsers();
        if (!users.isEmpty()) {
            log.info("👥 Active WebSocket users:");
            users.forEach(u ->
                    log.info(" - {} ({} session{})", u.getName(), u.getSessions().size(),
                            u.getSessions().size() > 1 ? "s" : ""));
        }
    }
}
