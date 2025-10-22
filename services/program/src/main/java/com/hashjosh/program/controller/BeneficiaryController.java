package com.hashjosh.program.controller;

import com.hashjosh.program.dto.BeneficiaryDto;
import com.hashjosh.program.service.BeneficiaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/beneficiary")
@RequiredArgsConstructor
@Slf4j
public class BeneficiaryController {

    private final BeneficiaryService beneficiaryService;

    @GetMapping
    public ResponseEntity<List<BeneficiaryDto>> getAllBeneficiaries() {
        return ResponseEntity.ok(beneficiaryService.getAllBeneficiaries());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BeneficiaryDto> getBeneficiaryById(@PathVariable UUID id) {
        return ResponseEntity.ok(beneficiaryService.getBeneficiaryById(id));
    }

    @PostMapping
    public ResponseEntity<BeneficiaryDto> createBeneficiary(@RequestBody BeneficiaryDto beneficiaryDto) {
        return new ResponseEntity<>(beneficiaryService.createBeneficiary(beneficiaryDto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BeneficiaryDto> updateBeneficiary(@PathVariable UUID id, 
                                                           @RequestBody BeneficiaryDto beneficiaryDto) {
        return ResponseEntity.ok(beneficiaryService.updateBeneficiary(id, beneficiaryDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBeneficiary(@PathVariable UUID id) {
        beneficiaryService.deleteBeneficiary(id);
        return ResponseEntity.noContent().build();
    }
}