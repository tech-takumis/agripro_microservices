package com.example.agriculture.controller;

import com.example.agriculture.dto.beneficiary.BeneficiaryRequest;
import com.example.agriculture.dto.beneficiary.BeneficiaryResponse;
import com.example.agriculture.service.BeneficiaryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/agriculture/beneficiaries")
public class BeneficiaryController {

    private final BeneficiaryService beneficiaryService;

    @PostMapping
    public ResponseEntity<BeneficiaryResponse> createBeneficiary(
            @RequestBody @Valid BeneficiaryRequest request) {
        return ResponseEntity.ok(beneficiaryService.createBeneficiary(request));
    }


    @GetMapping("/{id}")
    public ResponseEntity<BeneficiaryResponse> getBeneficiary(
            @PathVariable UUID id) {
        return ResponseEntity.ok(beneficiaryService.getBeneficiary(id));
    }

    @GetMapping("/program/{programId}")
    public ResponseEntity<Page<BeneficiaryResponse>> getBeneficiariesByProgram(
            @PathVariable UUID programId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return ResponseEntity.ok(beneficiaryService.getBeneficiariesByProgram(programId, pageable));
    }

    @GetMapping
    public ResponseEntity<Page<BeneficiaryResponse>> getAllBeneficiaries(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return ResponseEntity.ok(beneficiaryService.getAllBeneficiaries(pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BeneficiaryResponse> updateBeneficiary(
            @PathVariable UUID id,
            @RequestBody @Valid BeneficiaryRequest request) {
        return ResponseEntity.ok(beneficiaryService.updateBeneficiary(id, request));
    }

    @PutMapping("/{programId}/assign/all/farmers")
    public ResponseEntity<Void> assignAllBeneficiariesToProgram(
            @PathVariable("programId") UUID programId
    ) {
        beneficiaryService.assignAllFarmerBeneficiariesToProgram(programId);
        return ResponseEntity.ok().build();
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBeneficiary(@PathVariable UUID id) {
        beneficiaryService.deleteBeneficiary(id);
        return ResponseEntity.noContent().build();
    }
}
