package com.hashjosh.application.dto.batch;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BatchRequestDTO {
    String name;
    String description;
    LocalDateTime startDate;
    LocalDateTime endDate;
    boolean isAvailable;
    UUID  applicationTypeId;
}
