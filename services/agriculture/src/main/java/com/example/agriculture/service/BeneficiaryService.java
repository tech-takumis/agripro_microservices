package com.example.agriculture.service;

import com.example.agriculture.dto.beneficiary.BeneficiaryRequest;
import com.example.agriculture.dto.beneficiary.BeneficiaryResponse;
import com.example.agriculture.entity.Beneficiary;
import com.example.agriculture.entity.Program;
import com.example.agriculture.exception.UserException;
import com.example.agriculture.mapper.BeneficiaryMapper;
import com.example.agriculture.repository.BeneficiaryRepository;
import com.example.agriculture.repository.ProgramRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class BeneficiaryService {
    private final BeneficiaryMapper beneficiaryMapper;
    private final ProgramRepository programRepository;
    private final BeneficiaryRepository beneficiaryRepository;

    @Transactional
    public BeneficiaryResponse createBeneficiary(BeneficiaryRequest request) {
        // Check if beneficiary already exists for this program
        beneficiaryRepository.findByUserIdAndProgramId(request.getUserId(), request.getProgramId())
                .ifPresent(b -> {
                    throw new UserException("Beneficiary already exists in this program", HttpStatus.BAD_REQUEST.value());
                });

        Program program = programRepository.findById(request.getProgramId())
                .orElseThrow(() -> new UserException("Program not found", HttpStatus.NOT_FOUND.value()));

        Beneficiary beneficiary = beneficiaryMapper.toBeneficiaryEntity(request);
        beneficiary.setProgram(program);

        return beneficiaryMapper.toBeneficiaryResponse(beneficiaryRepository.save(beneficiary));
    }

    @Transactional(readOnly = true)
    public BeneficiaryResponse getBeneficiary(UUID id) {
        return beneficiaryMapper.toBeneficiaryResponse(
                beneficiaryRepository.findById(id)
                        .orElseThrow(() -> new UserException("Beneficiary not found", HttpStatus.NOT_FOUND.value()))
        );
    }

    @Transactional(readOnly = true)
    public Page<BeneficiaryResponse> getAllBeneficiaries(Pageable pageable) {
        return beneficiaryRepository.findAll(pageable)
                .map(beneficiaryMapper::toBeneficiaryResponse);
    }

    @Transactional
    public BeneficiaryResponse updateBeneficiary(UUID id, BeneficiaryRequest request) {
        Beneficiary beneficiary = beneficiaryRepository.findById(id)
                .orElseThrow(() -> new UserException("Beneficiary not found", HttpStatus.NOT_FOUND.value()));

        // If program is being changed, verify new program exists
        if (!beneficiary.getProgram().getId().equals(request.getProgramId())) {
            Program newProgram = programRepository.findById(request.getProgramId())
                    .orElseThrow(() -> new UserException("Program not found", HttpStatus.NOT_FOUND.value()));
            beneficiary.setProgram(newProgram);
        }

        beneficiary.setUserId(request.getUserId());
        beneficiary.setType(request.getType());

        return beneficiaryMapper.toBeneficiaryResponse(beneficiaryRepository.save(beneficiary));
    }

    @Transactional
    public void deleteBeneficiary(UUID id) {
        if (!beneficiaryRepository.existsById(id)) {
            throw new UserException("Beneficiary not found", HttpStatus.NOT_FOUND.value());
        }
        beneficiaryRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Page<BeneficiaryResponse> getBeneficiariesByProgram(UUID programId, Pageable pageable) {
        if (!programRepository.existsById(programId)) {
            throw new UserException("Program not found", HttpStatus.NOT_FOUND.value());
        }
        return beneficiaryRepository.findByProgramId(programId, pageable)
                .map(beneficiaryMapper::toBeneficiaryResponse);
    }
}
