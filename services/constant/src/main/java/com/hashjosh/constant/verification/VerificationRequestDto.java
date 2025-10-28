package com.hashjosh.constant.verification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerificationRequestDto {
    private String report;
    private String status;
    private String rejectionReason;
}
