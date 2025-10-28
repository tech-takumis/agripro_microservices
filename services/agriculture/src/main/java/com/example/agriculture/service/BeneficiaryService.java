package com.example.agriculture.service;

import com.example.agriculture.clients.FarmerClient;
import com.example.agriculture.config.CustomUserDetails;
import com.example.agriculture.dto.beneficiary.BeneficiaryRequest;
import com.example.agriculture.dto.beneficiary.BeneficiaryResponse;
import com.example.agriculture.entity.Beneficiary;
import com.example.agriculture.entity.Program;
import com.example.agriculture.exception.ApiException;
import com.example.agriculture.mapper.BeneficiaryMapper;
import com.example.agriculture.repository.BeneficiaryRepository;
import com.example.agriculture.repository.ProgramRepository;
import com.hashjosh.constant.farmer.FarmerReponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class BeneficiaryService {
    private final BeneficiaryMapper beneficiaryMapper;
    private final ProgramRepository programRepository;
    private final BeneficiaryRepository beneficiaryRepository;
    private final FarmerClient farmerClient;

    @Transactional
    public BeneficiaryResponse createBeneficiary(BeneficiaryRequest request) {
        // Check if beneficiary already exists for this program
        beneficiaryRepository.findByUserIdAndProgramId(request.getUserId(), request.getProgramId())
                .ifPresent(b -> {
                    throw ApiException.notFound(String.format("Beneficiary with userId %s already exists for program %s",
                            request.getUserId(), request.getProgramId()));
                });

        Program program = programRepository.findById(request.getProgramId())
                .orElseThrow(() -> ApiException.notFound(String.format("Program with ID %s not found", request.getProgramId())));

        Beneficiary beneficiary = beneficiaryMapper.toBeneficiaryEntity(request);
        beneficiary.setProgram(program);

        return beneficiaryMapper.toBeneficiaryResponse(beneficiaryRepository.save(beneficiary));
    }

    @Transactional(readOnly = true)
    public BeneficiaryResponse getBeneficiary(UUID id) {
        return beneficiaryMapper.toBeneficiaryResponse(
                beneficiaryRepository.findById(id)
                        .orElseThrow(() -> ApiException.notFound(String.format("Beneficiary with ID %s not found", id)))
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
                .orElseThrow(() -> ApiException.notFound(String.format("Beneficiary with ID %s not found", id)));

        // If program is being changed, verify new program exists
        if (!beneficiary.getProgram().getId().equals(request.getProgramId())) {
            Program newProgram = programRepository.findById(request.getProgramId())
                    .orElseThrow(() -> ApiException.notFound(String.format("Program with ID %s not found", request.getProgramId())));
            beneficiary.setProgram(newProgram);
        }

        beneficiary.setUserId(request.getUserId() != null ? request.getUserId() : beneficiary.getUserId());
        beneficiary.setType(request.getType() != null ? request.getType() : beneficiary.getType());

        return beneficiaryMapper.toBeneficiaryResponse(beneficiaryRepository.save(beneficiary));
    }

    @Transactional
    public void deleteBeneficiary(UUID id) {
        if (!beneficiaryRepository.existsById(id)) {
            throw ApiException.notFound(String.format("Beneficiary with ID %s not found", id));
        }
        beneficiaryRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Page<BeneficiaryResponse> getBeneficiariesByProgram(UUID programId, Pageable pageable) {
        if (!programRepository.existsById(programId)) {
            throw ApiException.notFound("Program with ID " + programId + " not found");
        }
        return beneficiaryRepository.findByProgramId(programId, pageable)
                .map(beneficiaryMapper::toBeneficiaryResponse);
    }


    public void assignAllFarmerBeneficiariesToProgram(UUID programId) {

        CustomUserDetails user = (CustomUserDetails) org.springframework.security.core.context.SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        Program program = programRepository.findById(programId)
                .orElseThrow(() -> ApiException.notFound(String.format("Program with ID %s not found", programId)));

        List<FarmerReponse> farmers = farmerClient.getAllFarmers(String.valueOf(user.getUserId()));

        // Fix: clear and add to the existing collection, do not replace it
        if (program.getBeneficiaries() == null) {
            program.setBeneficiaries(new ArrayList<>());
        } else {
            program.getBeneficiaries().clear();
        }

        for (FarmerReponse farmer : farmers) {
            Beneficiary beneficiary = new Beneficiary();
            beneficiary.setUserId(farmer.getId());
            beneficiary.setType("FARMER");
            beneficiary.setProgram(program);
            beneficiary.setActive(true);
            beneficiary.setAssignAt(LocalDateTime.now());
            beneficiaryRepository.save(beneficiary);
            program.addBeneficiary(beneficiary);
        }

        programRepository.save(program);
        log.info("Assigned {} farmers to program {}", farmers.size(), programId);
    }
}
