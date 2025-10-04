package com.hashjosh.program.mapper;

import com.hashjosh.constant.program.dto.ProgramCreateRequestDto;
import com.hashjosh.constant.program.dto.ProgramResponseDto;
import com.hashjosh.program.entity.Program;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ProgramMapper {
    public ProgramResponseDto toDto(Program program) {
        return ProgramResponseDto.builder()
                .id(program.getId())
                .name(program.getName())
                .type(program.getType())
                .status(program.getStatus())
                .completion(program.getCompletion())
                .extraFields(program.getExtraFields())
                .createdAt(program.getCreatedAt())
                .updatedAt(program.getUpdatedAt())
                .build();
    }

    public Program toEntity(ProgramCreateRequestDto dto) {
        return Program.builder()
                .name(dto.getName())
                .type(dto.getType())
                .status(dto.getStatus())
                .completion(dto.getCompletion())
                .extraFields(dto.getExtraFields())
                .createdAt(LocalDateTime.now())
                .build();
    }
}
