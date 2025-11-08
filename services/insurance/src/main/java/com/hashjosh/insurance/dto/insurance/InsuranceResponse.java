package com.hashjosh.insurance.dto.insurance;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InsuranceResponse {
    private UUID insuranceId;
    private UUID submissionId;
    private UUID submittedBy;
    private String status;
    private LocalDateTime createdAt;
}
