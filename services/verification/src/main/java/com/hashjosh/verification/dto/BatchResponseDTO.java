package com.hashjosh.verification.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BatchResponseDTO {
    UUID id;
    UUID applicationTypeId;
    String name;
    String description;
    boolean isAvailable;
    int maxApplications;
    LocalDateTime startDate;
    LocalDateTime endDate;
}
