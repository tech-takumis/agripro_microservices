package com.example.agriculture.controller;

import com.example.agriculture.dto.*;
import com.example.agriculture.service.InspectionRecordService;
import com.hashjosh.constant.ApplicationStatus;
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
public class InspectionRecordController {

    private final InspectionRecordService inspectionRecordService;

    @PutMapping("/{applicationId}")
    public ResponseEntity<Map<String, Object>> updateVerificationStatus(
            @PathVariable UUID applicationId,
            @RequestBody InspectionRecordRequest dto,
            HttpServletRequest request
    ) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of(
                        "message", "Application " + applicationId + " verification status updated successfully",
                        "data", inspectionRecordService.verify(applicationId, dto, request)
                ));
    }

    @GetMapping
    public ResponseEntity<List<InspectionRecordResponse>> getVerifications(
            @RequestParam(name = "status", required = false) List<String> statusParams,
            @RequestParam(name = "inspectionType", required = false) String inspectionType
    ) {
        List<ApplicationStatus> statuses = statusParams != null ?
                statusParams.stream()
                        .map(String::toUpperCase)
                        .map(ApplicationStatus::valueOf)
                        .collect(Collectors.toList()) :
                Collections.emptyList();

        List<InspectionRecordResponse> verificationList;

        if (!statuses.isEmpty() && inspectionType != null) {
            verificationList = inspectionRecordService.findByInspectionTypeAndStatuses(inspectionType, statuses);
        } else if (!statuses.isEmpty()) {
            verificationList = inspectionRecordService.findByStatuses(statuses);
        } else if (inspectionType != null) {
            verificationList = inspectionRecordService.findByInspectionTypeAndStatuses(inspectionType, Collections.emptyList());
        } else {
            verificationList = inspectionRecordService.findAllVerification();
        }

        return ResponseEntity.ok(verificationList);
    }

    @GetMapping("/{status}")
    public ResponseEntity<List<InspectionRecordResponse>> getVerificationsByStatus(
            @PathVariable String status
    ) {
        ApplicationStatus verificationStatus = ApplicationStatus.valueOf(status.toUpperCase());
        List<InspectionRecordResponse> verificationList = inspectionRecordService.findByStatus(verificationStatus);
        return ResponseEntity.ok(verificationList);
    }

    @GetMapping("/application/{applicationId}")
    public ResponseEntity<InspectionRecordResponse> getVerificationByApplicationId(
            @PathVariable UUID applicationId
    ) {
        return inspectionRecordService.findByApplicationId(applicationId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
