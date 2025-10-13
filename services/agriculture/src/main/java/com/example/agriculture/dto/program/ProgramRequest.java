package com.example.agriculture.dto.program;

import com.example.agriculture.dto.beneficiary.BeneficiaryRequest;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProgramRequest {
    private String programName;
    private String description;
    private float budget;
    private int completedPercentage;
    private String status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private List<BeneficiaryRequest> beneficiaries;
}
