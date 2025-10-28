package com.hashjosh.verification.controller;


import com.hashjosh.constant.verification.VerificationRequestDto;
import com.hashjosh.verification.dto.VerificationResponseDTO;
import com.hashjosh.verification.service.VerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/verification")
public class VerificationController {

    private final VerificationService verificationService;

    @PostMapping("/forwards")
    public void forwardToPCIC(
            @RequestBody List<UUID> applicationIds
    ) {
        verificationService.forwardToPcic(applicationIds);
    }

    @GetMapping
    public  ResponseEntity<List<VerificationResponseDTO>> getAllPendingVerifications(){
        List<VerificationResponseDTO> pendingVerifications = verificationService.getAllPendingVerifications();
        return ResponseEntity.ok(pendingVerifications);
    }

    @GetMapping("/{batchId}/submissions")
    public ResponseEntity<List<VerificationResponseDTO>> getSubmissionsByBatchId(
            @PathVariable String batchId
    ) {
        List<VerificationResponseDTO> submissions = verificationService.getSubmissionsByBatchId(UUID.fromString(batchId));
        return ResponseEntity.ok(submissions);
    }

    @PutMapping("/{submissionId}/review")
    public void review(
            @PathVariable UUID submissionId,
            @RequestBody VerificationRequestDto review
    ) {
        verificationService.applicationReview(submissionId,review);
    }
}
