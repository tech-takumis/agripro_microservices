package com.hashjosh.verification.controller;

import com.hashjosh.verification.dto.VerificationRequest;
import com.hashjosh.verification.dto.VerificationResponse;
import com.hashjosh.verification.enums.VerificationStatus;
import com.hashjosh.verification.service.VerificationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/verifications")
public class VerificationController {

    private final VerificationService verificationService;

    @PutMapping("/{applicationId}")
    public ResponseEntity<Map<String, Object>> updateVerificationStatus(
            @PathVariable UUID applicationId,
            @RequestBody VerificationRequest dto,
            HttpServletRequest request
    ) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of(
                        "message", "Application " + applicationId + " verification status updated successfully",
                        "data", verificationService.verify(applicationId, dto, request)
                ));
    }

    @GetMapping
    public ResponseEntity<List<VerificationResponse>> getVerifications(
            @RequestParam(name = "status", required = false) List<String> statusParams,
            @RequestParam(name = "inspectionType", required = false) String inspectionType
    ) {
        List<VerificationStatus> statuses = statusParams != null ?
                statusParams.stream()
                        .map(String::toUpperCase)
                        .map(VerificationStatus::valueOf)
                        .collect(Collectors.toList()) :
                Collections.emptyList();

        List<VerificationResponse> verificationList;

        if (!statuses.isEmpty() && inspectionType != null) {
            verificationList = verificationService.findByInspectionTypeAndStatuses(inspectionType, statuses);
        } else if (!statuses.isEmpty()) {
            verificationList = verificationService.findByStatuses(statuses);
        } else if (inspectionType != null) {
            verificationList = verificationService.findByInspectionTypeAndStatuses(inspectionType, Collections.emptyList());
        } else {
            verificationList = verificationService.findAllVerification();
        }

        return ResponseEntity.ok(verificationList);
    }

    @GetMapping("/{status}")
    public ResponseEntity<List<VerificationResponse>> getVerificationsByStatus(
            @PathVariable String status
    ) {
        VerificationStatus verificationStatus = VerificationStatus.valueOf(status.toUpperCase());
        List<VerificationResponse> verificationList = verificationService.findByStatus(verificationStatus);
        return ResponseEntity.ok(verificationList);
    }

    @GetMapping("/application/{applicationId}")
    public ResponseEntity<VerificationResponse> getVerificationByApplicationId(
            @PathVariable UUID applicationId
    ) {
        return verificationService.findByApplicationId(applicationId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
