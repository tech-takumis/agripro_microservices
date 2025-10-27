package com.example.agriculture.controller;


import com.hashjosh.constant.verification.VerificationRequestDto;
import com.example.agriculture.service.VerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/agriculture/verification")
public class VerificationController {

    private final VerificationService verificationService;

    @PutMapping("/{submissionId}/review")
    public void review(
            @PathVariable UUID submissionId,
            @RequestBody VerificationRequestDto review
    ) {
        verificationService.applicationReview(submissionId,review);
    }

    @PostMapping("/forwards")
    public void forwardToPCIC(
            @RequestBody List<UUID> applicationIds
            ) {
        verificationService.forwardToPcic(applicationIds);
    }
}
