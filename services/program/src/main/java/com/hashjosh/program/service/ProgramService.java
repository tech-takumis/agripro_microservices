package com.hashjosh.program.service;

import com.hashjosh.program.dto.ProgramDto;
import com.hashjosh.program.entity.Program;
import com.hashjosh.program.exception.ApiException;
import com.hashjosh.program.mapper.ProgramMapper;
import com.hashjosh.program.repository.ProgramRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProgramService {

    private final ProgramRepository programRepository;
    private final ProgramMapper programMapper;

    public List<ProgramDto> getAllPrograms() {
        return programRepository.findAll().stream()
                .map(programMapper::toDto)
                .collect(Collectors.toList());
    }

    public ProgramDto getProgramById(UUID id) {
        return programRepository.findById(id)
                .map(programMapper::toDto)
                .orElseThrow(() -> ApiException.notFound("Program not found with id: " + id));
    }

    @Transactional
    public ProgramDto createProgram(ProgramDto programDto) {
        Program program = programMapper.toEntity(programDto);
        program.setId(null); // Ensure new entity creation
        return programMapper.toDto(programRepository.save(program));
    }

    @Transactional
    public ProgramDto updateProgram(UUID id, ProgramDto programDto) {
        Program existingProgram = programRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("Program not found with id: " + id));

        Program updatedProgram = programMapper.toEntity(programDto);
        updatedProgram.setId(id);
        updatedProgram.setBeneficiaries(existingProgram.getBeneficiaries());
        
        return programMapper.toDto(programRepository.save(updatedProgram));
    }

    @Transactional
    public void deleteProgram(UUID id) {
        if (!programRepository.existsById(id)) {
            throw ApiException.notFound("Program not found with id: " + id);
        }
        programRepository.deleteById(id);
    }
}