package com.hashjosh.program.controller;

import com.hashjosh.constant.program.dto.ProgramCreateRequestDto;
import com.hashjosh.constant.program.dto.ProgramResponseDto;
import com.hashjosh.constant.program.dto.ProgramUpdateRequestDto;
import com.hashjosh.program.service.ProgramService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/programs")
@RequiredArgsConstructor
public class ProgramController {

    private final ProgramService service;

    @GetMapping
    public List<ProgramResponseDto> getAll() {
        return service.getAll();
    }


    @GetMapping("/{id}")
    public ProgramResponseDto getById(@PathVariable UUID id) {
        return service.getById(id);
    }

    @PostMapping
    public ProgramResponseDto create(@RequestBody ProgramCreateRequestDto dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    public ProgramResponseDto update(@PathVariable UUID id, @RequestBody ProgramUpdateRequestDto dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}