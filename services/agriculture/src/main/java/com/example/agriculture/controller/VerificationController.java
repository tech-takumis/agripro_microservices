package com.example.agriculture.controller;


import com.example.agriculture.dto.VerificationRequestDto;
import com.example.agriculture.service.VerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/agriculture/verification")
public class VerificationController {

    private final VerificationService verificationService;

    @PutMapping("/applications/{submissionId}/review")
    public void startReview(@PathVariable UUID submissionId) {
        verificationService.startReview(submissionId);
    }

    @PutMapping("/applications/{submissionId}/verify")
    public void completeVerification(@PathVariable UUID submissionId,
                                     @RequestBody VerificationRequestDto request) {
        verificationService.completeVerification(
                submissionId,
                request.getReport(),
                request.getStatus(),
                request.getRejectionReason()
        );
    }

    @PostMapping("/applications/{submissionId}/send-to-pcic")
    public void sendToPcic(@PathVariable UUID submissionId) {
        verificationService.sendToPcic(submissionId);
    }
}
