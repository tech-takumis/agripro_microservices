package com.hashjosh.insurance.service;

import com.hashjosh.insurance.dto.insurance.InsuranceRequestDTO;
import com.hashjosh.insurance.dto.insurance.InsuranceResponse;
import com.hashjosh.insurance.entity.Insurance;
import com.hashjosh.insurance.exception.ApiException;
import com.hashjosh.insurance.mapper.InsuranceMapper;
import com.hashjosh.insurance.repository.InsuranceRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InsuranceService {

    private final InsuranceRepository insuranceRepository;
    private final InsuranceMapper insuranceMapper;

    public InsuranceResponse findAll() {
        return insuranceRepository.findAll()
                .stream()
                .map(insuranceMapper::toInsuranceResponse)
                .reduce((first, second) -> second)
                .orElse(null);
    }

    public InsuranceResponse update(UUID insuranceId, @Valid InsuranceRequestDTO request) {
        return null;
    }

    public void delete(UUID insuranceId) {
        Insurance insurance = insuranceRepository.findById(insuranceId)
                .orElseThrow(() -> ApiException.notFound("Insurance not found"));

        insuranceRepository.delete(insurance);
    }
}
