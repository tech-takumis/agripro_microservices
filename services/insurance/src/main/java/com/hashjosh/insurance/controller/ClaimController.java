package com.hashjosh.insurance.controller;

import com.hashjosh.insurance.dto.claim.ClaimRequest;
import com.hashjosh.insurance.dto.claim.ClaimResponse;
import com.hashjosh.insurance.service.ClaimService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/claim")
public class ClaimController {

    private final ClaimService claimService;

    @PostMapping()
    public  ResponseEntity<ClaimResponse> createClaim(
            @RequestBody ClaimRequest request

    ) {
        ClaimResponse claimResponse = claimService.createClaim(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(claimResponse);
    }

    @GetMapping
    public ResponseEntity<List<ClaimResponse>> getAllClaims() {
        return ResponseEntity.ok(claimService.getAllClaims());
    }


    @PutMapping("/{claim-id}")
    public ResponseEntity<ClaimResponse> updateClaim(
            @PathVariable("claim-id") UUID claimId,
            @RequestBody ClaimRequest claim) {

        return  ResponseEntity.status(HttpStatus.CREATED)
                .body(claimService.updateClaim(claimId,claim));
    }

    @DeleteMapping("/{claim-id}")
    public ResponseEntity<Void> deleteClaim(
            @PathVariable("claim-id") UUID claimId
    ){
        claimService.delete(claimId);
        return ResponseEntity.noContent().build();
    }

}
