package com.example.agriculture.dto.program;

import com.example.agriculture.dto.beneficiary.BeneficiaryResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProgramResponse {

    private UUID programId;
    private String programName;
    private String description;
    private float budget;
    private int completedPercentage;
    private String status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private List<BeneficiaryResponse> beneficiaries;
}
