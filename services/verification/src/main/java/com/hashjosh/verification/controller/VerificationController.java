package com.hashjosh.verification.controller;


import com.hashjosh.verification.dto.VerificationRequest;
import com.hashjosh.verification.dto.VerificationResponse;
import com.hashjosh.verification.service.VerificationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/verify")
public class VerificationController {

    private final VerificationService verificationService;

    @PutMapping("/{applicationId}")
    public ResponseEntity<Map<String,Object>> verifyApplication(
            @PathVariable UUID applicationId,
            @RequestBody VerificationRequest dto,
            HttpServletRequest request
    ){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of(
                        "message", "Application id "+applicationId +"updated successfully",
                        "data", verificationService.verify(applicationId,dto,request)
                ));
    }

    /***
     *  Request param q = true/false, true will return all verified application by adjuster and
     *  false will return all not verified by adjuster
     */
    @GetMapping
    public ResponseEntity<List<VerificationResponse>> getVerifications(
            @RequestParam(name = "verifiedByAdjuster", required = false) Boolean isVerifiedByAdjuster,
            @RequestParam(name = "approvedByUnderwriter", required = false) Boolean isApprovedByUnderwriter

    ){
        List<VerificationResponse> verificationList;

        if(isVerifiedByAdjuster !=null && isApprovedByUnderwriter !=null){
            verificationList = verificationService.findAllVerifiedByAdjusterAndUnderwriter(isVerifiedByAdjuster,isApprovedByUnderwriter);
        } else if(isVerifiedByAdjuster != null){
            verificationList = verificationService.findAllVerifiedByAdjusterApplication(isVerifiedByAdjuster);
        } else if (isApprovedByUnderwriter != null) {
            verificationList = verificationService.findAllApprovedByUnderwriter(isApprovedByUnderwriter);
        }else {
            verificationList = verificationService.findAllVerification();
        }

        return ResponseEntity.ok(verificationList);
    };

}
