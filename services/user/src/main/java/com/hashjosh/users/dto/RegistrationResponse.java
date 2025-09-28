package com.hashjosh.users.dto;

import com.hashjosh.kafkacommon.user.TenantType;
import lombok.*;

import java.time.LocalDateTime;

public class RegistrationResponse {
    @Getter
    @Setter
    @Builder
    @Data
    public static class FarmerRegistrationResponse {
        private final String message;
        private final String username;
        private final String error;
        private final LocalDateTime timestamp;
        private final int status;
        private final boolean success;
    }

    @Getter
    @Setter
    @Builder
    @Data
    public static class StaffRegistrationResponse {

        private final String message;
        private final String username;
        private final String error;
        private final LocalDateTime timestamp;
        private final int status;
        private final boolean success;
    }
}
