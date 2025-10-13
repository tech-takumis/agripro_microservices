package com.example.agriculture.controller;

import com.example.agriculture.dto.program.ProgramRequest;
import com.example.agriculture.dto.program.ProgramResponse;
import com.example.agriculture.service.ProgramService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/agriculture/programs")
public class ProgramController {
    private final ProgramService programService;

    @PostMapping
    public ResponseEntity<ProgramResponse> createProgram(@RequestBody @Valid ProgramRequest request) {
        return ResponseEntity.ok(programService.createProgram(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProgramResponse> getProgram(@PathVariable UUID id) {
        return ResponseEntity.ok(programService.getProgram(id));
    }

    @GetMapping
    public ResponseEntity<Page<ProgramResponse>> getAllPrograms(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("startDate").descending());
        return ResponseEntity.ok(programService.getAllPrograms(pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProgramResponse> updateProgram(
            @PathVariable UUID id,
            @RequestBody @Valid ProgramRequest request) {
        return ResponseEntity.ok(programService.updateProgram(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProgram(@PathVariable UUID id) {
        programService.deleteProgram(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{programId}/beneficiaries")
    public ResponseEntity<ProgramResponse> addBeneficiaries(
            @PathVariable UUID programId,
            @RequestBody List<UUID> beneficiaryIds) {
        return ResponseEntity.ok(programService.addBeneficiariesToProgram(programId, beneficiaryIds));
    }

    @DeleteMapping("/{programId}/beneficiaries")
    public ResponseEntity<ProgramResponse> removeBeneficiaries(
            @PathVariable UUID programId,
            @RequestBody List<UUID> beneficiaryIds) {
        return ResponseEntity.ok(programService.removeBeneficiariesFromProgram(programId, beneficiaryIds));
    }

    @GetMapping("/count/active")
    public ResponseEntity<Integer> getActiveCount() {
        return ResponseEntity.ok(programService.countActivePrograms());
    }
}
