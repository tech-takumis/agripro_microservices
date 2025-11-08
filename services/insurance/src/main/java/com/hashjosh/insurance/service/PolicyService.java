package com.hashjosh.insurance.service;

import com.hashjosh.constant.pcic.enums.PolicyStatus;
import com.hashjosh.insurance.dto.policy.PolicyRequest;
import com.hashjosh.insurance.entity.Policy;
import com.hashjosh.insurance.mapper.PolicyMapper;
import com.hashjosh.insurance.repository.PolicyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PolicyService {

    private final PolicyRepository policyRepository;
    private final PolicyMapper policyMapper;

    public Policy createPolicy(PolicyRequest request) {

        Policy policy = policyMapper.toPolicyEntity(request);

        return policyRepository.save(policy);
    }

}
