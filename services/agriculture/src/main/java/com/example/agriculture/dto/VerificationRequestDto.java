package com.example.agriculture.dto;

import com.example.agriculture.enums.VerificationStatus;
import lombok.Data;

@Data
public class VerificationRequestDto {
    private String report;
    private VerificationStatus status;
    private String rejectionReason;
}
