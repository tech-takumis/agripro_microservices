package com.example.agriculture.mapper;

import com.example.agriculture.dto.program.ProgramRequest;
import com.example.agriculture.dto.program.ProgramResponse;
import com.example.agriculture.entity.Program;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProgramMapper {

    private final BeneficiaryMapper beneficiaryMapper;

    public Program toProgramEntity(ProgramRequest request) {
        return Program.builder()
                .name(request.getProgramName())
                .description(request.getDescription())
                .budget(request.getBudget())
                .completedPercentage(request.getCompletedPercentage())
                .status(request.getStatus())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .beneficiaries(new ArrayList<>()) // Initialize empty list, beneficiaries will be added separately
                .build();
    }

    public ProgramResponse toProgramResponse(Program program) {
        return ProgramResponse.builder()
                .programId(program.getId())
                .programName(program.getName())
                .description(program.getDescription())
                .budget(program.getBudget())
                .completedPercentage(program.getCompletedPercentage())
                .status(program.getStatus())
                .startDate(program.getStartDate())
                .endDate(program.getEndDate())
                .beneficiaries(program.getBeneficiaries() != null
                    ? program.getBeneficiaries().stream()
                        .map(beneficiaryMapper::toBeneficiaryResponse)
                        .collect(Collectors.toList())
                    : new ArrayList<>())
                .build();
    }

    public void updateProgramFromRequest(Program program, ProgramRequest request) {
        program.setName(request.getProgramName() != null ? request.getProgramName() : program.getName());
        program.setDescription(request.getDescription() != null ? request.getDescription() : program.getDescription());
        program.setBudget(request.getBudget() != 0 ? request.getBudget() : program.getBudget());
        program.setCompletedPercentage(request.getCompletedPercentage() != 0 ? request.getCompletedPercentage() : program.getCompletedPercentage());
        program.setStatus(request.getStatus() != null ? request.getStatus() : program.getStatus());
        program.setStartDate(request.getStartDate() != null ? request.getStartDate() : program.getStartDate());
        program.setEndDate(request.getEndDate() != null ? request.getEndDate() : program.getEndDate());
    }
}
