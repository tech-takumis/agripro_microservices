package com.hashjosh.insurance.dto.policy;

import com.hashjosh.constant.pcic.enums.PolicyStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
public class PolicyRequest {
    private UUID insuranceId;
    private String  policyNumber;
    private Double coverageAmount;
    private PolicyStatus status;
}
