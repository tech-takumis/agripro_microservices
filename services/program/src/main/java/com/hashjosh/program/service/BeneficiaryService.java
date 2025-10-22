
package com.hashjosh.program.service;

import com.hashjosh.program.dto.BeneficiaryDto;
import com.hashjosh.program.entity.Beneficiary;
import com.hashjosh.program.exception.ApiException;
import com.hashjosh.program.mapper.BeneficiaryMapper;
import com.hashjosh.program.repository.BeneficiaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BeneficiaryService {

    private final BeneficiaryRepository beneficiaryRepository;
    private final BeneficiaryMapper beneficiaryMapper;

    public List<BeneficiaryDto> getAllBeneficiaries() {
        return beneficiaryRepository.findAll().stream()
                .map(beneficiaryMapper::toDto)
                .collect(Collectors.toList());
    }

    public BeneficiaryDto getBeneficiaryById(UUID id) {
        return beneficiaryRepository.findById(id)
                .map(beneficiaryMapper::toDto)
                .orElseThrow(() -> ApiException.notFound("Beneficiary not found with id: " + id));
    }

    @Transactional
    public BeneficiaryDto createBeneficiary(BeneficiaryDto beneficiaryDto) {
        Beneficiary beneficiary = beneficiaryMapper.toEntity(beneficiaryDto);
        beneficiary.setId(null); // Ensure new entity creation
        return beneficiaryMapper.toDto(beneficiaryRepository.save(beneficiary));
    }

    @Transactional
    public BeneficiaryDto updateBeneficiary(UUID id, BeneficiaryDto beneficiaryDto) {
        if (!beneficiaryRepository.existsById(id)) {
            throw ApiException.notFound("Beneficiary not found with id: " + id);
        }

        Beneficiary beneficiary = beneficiaryMapper.toEntity(beneficiaryDto);
        beneficiary.setId(id);
        return beneficiaryMapper.toDto(beneficiaryRepository.save(beneficiary));
    }

    @Transactional
    public void deleteBeneficiary(UUID id) {
        if (!beneficiaryRepository.existsById(id)) {
            throw ApiException.notFound("Beneficiary not found with id: " + id);
        }
        beneficiaryRepository.deleteById(id);
    }
}
