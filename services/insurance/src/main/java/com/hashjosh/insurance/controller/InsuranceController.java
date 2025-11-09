package com.hashjosh.insurance.controller;

import com.hashjosh.insurance.dto.insurance.InsuranceRequestDTO;
import com.hashjosh.insurance.dto.insurance.InsuranceResponse;
import com.hashjosh.insurance.service.InsuranceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/insurance")
@RequiredArgsConstructor
public class InsuranceController {

    private final InsuranceService insuranceService;

    @GetMapping
    public ResponseEntity<InsuranceResponse> getAllInsurance(){
        return ResponseEntity.ok(insuranceService.findAll());
    }

    @PutMapping("/{insurance-Id}")
    public ResponseEntity<InsuranceResponse> updateInsurance(
            @PathVariable("insurance-Id")UUID insuranceId,
            @RequestBody @Valid InsuranceRequestDTO request
            ){
        return ResponseEntity.ok(insuranceService.update(insuranceId,request));
    }

    @DeleteMapping("/{insurance-Id}")
    public ResponseEntity<Void> deleteInsurance(
            @PathVariable("insurance-Id") UUID insuranceId
    ){
        insuranceService.delete(insuranceId);
        return ResponseEntity.ok().build();
    }
}
