package com.hashjosh.insurance.mapper;

import com.hashjosh.constant.pcic.enums.PolicyStatus;
import com.hashjosh.insurance.dto.policy.PolicyRequest;
import com.hashjosh.insurance.entity.Policy;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class PolicyMapper {

    public Policy toPolicyEntity(PolicyRequest request){
        return Policy.builder()
                .policyNumber(request.getPolicyNumber())
                .coverageAmount(request.getCoverageAmount())
                .status(request.getStatus())
                .build();
    }
}
