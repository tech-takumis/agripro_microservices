package com.example.agriculture.dto.beneficiary;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BeneficiaryResponse {
    private UUID beneficiaryId;
    private UUID userId;
    private String type;
}
