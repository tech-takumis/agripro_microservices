package com.hashjosh.transaction.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProgramDto {
    private UUID id;
    private String name;
    private String description;
    private float budget;
    private int completedPercentage;
    private String status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}