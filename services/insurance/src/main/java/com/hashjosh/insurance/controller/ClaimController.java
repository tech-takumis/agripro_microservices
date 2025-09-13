package com.hashjosh.insurance.controller;


import com.hashjosh.insurance.dto.ClaimRequest;
import com.hashjosh.insurance.service.ClaimService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/claim")
public class ClaimController {

    private final ClaimService claimService;

    @PutMapping("/{claim-id}")
    public ResponseEntity<Map<String,Object>> updateClaim(
            @PathVariable("claim-id") UUID claimId,
            @RequestBody ClaimRequest claim,
            HttpServletRequest request) {

        return  ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of(
                        "message", "Claim updated successfully!",
                        "data", claimService.updateClaim(claimId,claim,request)
                ));

    }


}
