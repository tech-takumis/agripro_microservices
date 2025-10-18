package com.hashjosh.communication.dto;

import com.hashjosh.constant.communication.enums.ServiceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class FarmerResponseDto {
    private UUID id;
    private UUID userId;
    private String firstName;
    private String lastName;
    private String middleName;
    private String phoneNumber;
    private String serviceType;
    private String username;
    private String email;
    private LocalDateTime createdAt;
}
