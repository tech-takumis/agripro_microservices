package com.example.agriculture.service;

import com.example.agriculture.dto.program.ProgramRequest;
import com.example.agriculture.dto.program.ProgramResponse;
import com.example.agriculture.entity.Beneficiary;
import com.example.agriculture.entity.Program;
import com.example.agriculture.exception.ApiException;
import com.example.agriculture.mapper.BeneficiaryMapper;
import com.example.agriculture.mapper.ProgramMapper;
import com.example.agriculture.repository.BeneficiaryRepository;
import com.example.agriculture.repository.ProgramRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProgramService {

    private final ProgramRepository programRepository;
    private final BeneficiaryRepository beneficiaryRepository;
    private final ProgramMapper programMapper;
    private final BeneficiaryMapper beneficiaryMapper;

    @Transactional
    public ProgramResponse createProgram(ProgramRequest request) {
        log.info("Creating new program: {}", request.getProgramName());

        Program program = programMapper.toProgramEntity(request);
        program = programRepository.save(program);

        // Handle beneficiaries if provided
        if (request.getBeneficiaries() != null && !request.getBeneficiaries().isEmpty()) {
            Program finalProgram = program;
            request.getBeneficiaries().forEach(beneficiaryRequest -> {
                Beneficiary beneficiary = beneficiaryMapper.toBeneficiaryEntity(beneficiaryRequest);
                beneficiary.setProgram(finalProgram);
                finalProgram.addBeneficiary(beneficiaryRepository.save(beneficiary));
            });
        }

        return programMapper.toProgramResponse(program);
    }

    @Transactional(readOnly = true)
    public ProgramResponse getProgram(UUID id) {
        return programMapper.toProgramResponse(findProgramById(id));
    }

    @Transactional(readOnly = true)
    public Page<ProgramResponse> getAllPrograms(Pageable pageable) {
        return programRepository.findAll(pageable)
                .map(programMapper::toProgramResponse);
    }

    @Transactional
    public ProgramResponse updateProgram(UUID id, ProgramRequest request) {
        log.info("Updating program with ID: {}", id);

        Program program = findProgramById(id);
        programMapper.updateProgramFromRequest(program, request);

        // Update beneficiaries if provided
        if (request.getBeneficiaries() != null) {
            // Remove existing beneficiaries
            program.getBeneficiaries().clear();

            // Add new beneficiaries
            request.getBeneficiaries().forEach(beneficiaryRequest -> {
                Beneficiary beneficiary = beneficiaryMapper.toBeneficiaryEntity(beneficiaryRequest);
                beneficiary.setProgram(program);
                program.addBeneficiary(beneficiaryRepository.save(beneficiary));
            });
        }

        return programMapper.toProgramResponse(programRepository.save(program));
    }

    @Transactional
    public void deleteProgram(UUID id) {
        log.info("Deleting program with ID: {}", id);

        Program program = findProgramById(id);
        programRepository.delete(program);
    }

    @Transactional
    public ProgramResponse addBeneficiariesToProgram(UUID programId, List<UUID> beneficiaryIds) {
        Program program = findProgramById(programId);

        beneficiaryIds.forEach(beneficiaryId -> {
            Beneficiary beneficiary = beneficiaryRepository.findById(beneficiaryId)
                    .orElseThrow(() -> ApiException.notFound(String.format("Beneficiary with ID %s not found", beneficiaryId)));

            if (!program.getBeneficiaries().contains(beneficiary)) {
                program.addBeneficiary(beneficiary);
            }
        });

        return programMapper.toProgramResponse(programRepository.save(program));
    }

    @Transactional
    public ProgramResponse removeBeneficiariesFromProgram(UUID programId, List<UUID> beneficiaryIds) {
        Program program = findProgramById(programId);

        beneficiaryIds.forEach(beneficiaryId -> {
            program.getBeneficiaries().stream()
                    .filter(b -> b.getId().equals(beneficiaryId))
                    .findFirst()
                    .ifPresent(program::removeBeneficiary);
        });

        return programMapper.toProgramResponse(programRepository.save(program));
    }

    @Transactional(readOnly = true)
    public int countActivePrograms() {
        return programRepository.countByStatus("ACTIVE");
    }

    private Program findProgramById(UUID id) {
        return programRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("Program with ID " + id + " not found"));
    }
}
