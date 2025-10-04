package com.hashjosh.program.controller;

import com.hashjosh.constant.program.dto.ScheduleRequestDto;
import com.hashjosh.constant.program.dto.ScheduleResponseDto;
import com.hashjosh.constant.program.dto.ScheduleUpdateRequestDto;
import com.hashjosh.program.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService service;

    @GetMapping
    public List<ScheduleResponseDto> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public ScheduleResponseDto getById(@PathVariable UUID id) {
        return service.getById(id);
    }

    @PostMapping
    public ScheduleResponseDto create(@RequestBody ScheduleRequestDto dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    public ScheduleResponseDto update(@PathVariable UUID id,
                                      @RequestBody ScheduleUpdateRequestDto dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}