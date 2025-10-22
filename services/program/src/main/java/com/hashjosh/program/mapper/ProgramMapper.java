package com.hashjosh.transaction.mapper;

import com.hashjosh.transaction.dto.ProgramDto;
import com.hashjosh.transaction.entity.Program;
import org.springframework.stereotype.Component;

@Component
public class ProgramMapper {
    
    public ProgramDto toDto(Program program) {
        if (program == null) return null;
        
        return ProgramDto.builder()
                .id(program.getId())
                .name(program.getName())
                .description(program.getDescription())
                .budget(program.getBudget())
                .completedPercentage(program.getCompletedPercentage())
                .status(program.getStatus())
                .startDate(program.getStartDate())
                .endDate(program.getEndDate())
                .build();
    }

    public Program toEntity(ProgramDto dto) {
        if (dto == null) return null;
        
        return Program.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .budget(dto.getBudget())
                .completedPercentage(dto.getCompletedPercentage())
                .status(dto.getStatus())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .build();
    }
}