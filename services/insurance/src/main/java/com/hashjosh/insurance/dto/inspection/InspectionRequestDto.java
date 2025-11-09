package com.hashjosh.insurance.dto.inspection;

import com.hashjosh.constant.pcic.enums.InspectionStatus;
import com.hashjosh.insurance.dto.policy.PolicyRequest;
import lombok.Data;

@Data
public class InspectionRequestDto {
    private InspectionStatus status;
    private String comments;
    private PolicyRequest policy;
}
