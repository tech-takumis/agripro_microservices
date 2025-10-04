package com.hashjosh.program.service;

import com.hashjosh.constant.program.dto.ScheduleRequestDto;
import com.hashjosh.constant.program.dto.ScheduleResponseDto;
import com.hashjosh.constant.program.dto.ScheduleUpdateRequestDto;
import com.hashjosh.program.mapper.ScheduleMapper;
import com.hashjosh.program.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository repository;
    private final ScheduleMapper mapper;

    public List<ScheduleResponseDto> getAll() {
        return repository.findAll()
                .stream().map(mapper::toDto).collect(Collectors.toList());
    }

    public ScheduleResponseDto getById(UUID id) {
        return mapper.toDto(repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Schedule not found")));
    }

    public ScheduleResponseDto create(ScheduleRequestDto dto) {
        return mapper.toDto(repository.save(mapper.toEntity(dto)));
    }

    public ScheduleResponseDto update(UUID id, ScheduleUpdateRequestDto dto) {
        var existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Schedule not found"));
        existing.setType(dto.getType());
        existing.setScheduleDate(dto.getScheduleDate());
        existing.setPriority(dto.getPriority());
        existing.setMetaData(dto.getMetaData());
        return mapper.toDto(repository.save(existing));
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }
}
