package com.hashjosh.program.service;

import com.hashjosh.constant.program.dto.ProgramCreateRequestDto;
import com.hashjosh.constant.program.dto.ProgramResponseDto;
import com.hashjosh.constant.program.dto.ProgramUpdateRequestDto;
import com.hashjosh.program.mapper.ProgramMapper;
import com.hashjosh.program.repository.ProgramRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProgramService {

    private final ProgramRepository repository;
    private final ProgramMapper mapper;


    public List<ProgramResponseDto> getAll() {
        return repository.findAll()
                .stream().map(mapper::toDto).collect(Collectors.toList());
    }

    public ProgramResponseDto getById(UUID id) {
        return mapper.toDto(repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Program not found")));
    }

    public ProgramResponseDto create(ProgramCreateRequestDto dto) {
        return mapper.toDto(repository.save(mapper.toEntity(dto)));
    }

    public ProgramResponseDto update(UUID id, ProgramUpdateRequestDto dto) {
        var existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Program not found"));
        existing.setName(dto.getName());
        existing.setType(dto.getType());
        existing.setStatus(dto.getStatus());
        existing.setCompletion(dto.getCompletion());
        existing.setExtraFields(dto.getExtraFields());
        return mapper.toDto(repository.save(existing));
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }
}
