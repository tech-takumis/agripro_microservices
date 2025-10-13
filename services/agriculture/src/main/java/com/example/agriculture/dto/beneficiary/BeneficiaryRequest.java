package com.example.agriculture.dto.beneficiary;

import lombok.*;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BeneficiaryRequest {
    private UUID userId;
    private UUID programId;
    private String type;
}
