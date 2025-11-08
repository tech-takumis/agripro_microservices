package com.hashjosh.insurance.dto.insurance;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class InsuranceRequestDTO {
    private UUID submissionId;
    private UUID submittedBy;
    private String status;
}
