package com.hashjosh.verification.controller;

import com.hashjosh.verification.dto.VerificationRequestDTO;
import com.hashjosh.verification.dto.VerificationResponseDTO;
import com.hashjosh.verification.service.VerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/verifications")
@RequiredArgsConstructor
public class VerificationController {

    private final VerificationService verificationService;

    @GetMapping
    public ResponseEntity<List<VerificationResponseDTO>> getAllVerifications() {
        return ResponseEntity.ok(verificationService.getAllVerifications());
    }

    @GetMapping("/{id}")
    public ResponseEntity<VerificationResponseDTO> getVerificationById(@PathVariable UUID id) {
        return ResponseEntity.ok(verificationService.getVerificationById(id));
    }

    @GetMapping("/submission/{submissionId}")
    public ResponseEntity<List<VerificationResponseDTO>> getVerificationsBySubmissionId(
            @PathVariable UUID submissionId) {
        return ResponseEntity.ok(verificationService.getVerificationsBySubmissionId(submissionId));
    }

    @PostMapping
    public ResponseEntity<VerificationResponseDTO> createVerification(
            @RequestBody VerificationRequestDTO requestDTO) {
        return new ResponseEntity<>(verificationService.createVerification(requestDTO), 
                HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<VerificationResponseDTO> updateVerification(
            @PathVariable UUID id, 
            @RequestBody VerificationRequestDTO requestDTO) {
        return ResponseEntity.ok(verificationService.updateVerification(id, requestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVerification(@PathVariable UUID id) {
        verificationService.deleteVerification(id);
        return ResponseEntity.noContent().build();
    }
}