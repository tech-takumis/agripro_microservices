package com.example.agriculture.controller;


import com.hashjosh.constant.verification.VerificationRequestDto;
import com.example.agriculture.service.VerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/agriculture/verification")
public class VerificationController {

    private final VerificationService verificationService;

    @PostMapping()
    public ResponseEntity<String> createVerificationRecord(
            @RequestBody VerificationRequestDto request
    ) {
        verificationService.createVerificationRecord(request);
        return ResponseEntity.ok("Verification record created");
    }

    @PutMapping("/applications/{submissionId}/review")
    public void startReview(@PathVariable UUID submissionId) {
        verificationService.startReview(submissionId);
    }

    @PostMapping("/applications/{submissionId}/send-to-pcic")
    public void sendToPcic(@PathVariable UUID submissionId) {
        verificationService.sendToPcic(submissionId);
    }
}
