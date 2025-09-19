package com.hashjosh.application.controller;

import com.hashjosh.application.configs.CustomUserDetails;
import com.hashjosh.application.dto.BatchApplicationResponse;
import com.hashjosh.application.dto.BatchRequest;
import com.hashjosh.application.dto.BatchResponse;
import com.hashjosh.application.exceptions.ApplicationException;
import com.hashjosh.application.model.ApplicationBatch;
import com.hashjosh.application.enums.BatchStatus;
import com.hashjosh.application.service.ApplicationBatchService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/batches")
@RequiredArgsConstructor
public class ApplicationBatchController {

    private final ApplicationBatchService batchService;

    @PostMapping
    public ResponseEntity<BatchResponse> createBatch(
            @RequestBody BatchRequest request) {


        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!(principal instanceof CustomUserDetails userDetails)) {
            throw new ApplicationException(
                    "User not authenticated can't get the principal in application batch controller",
                    HttpStatus.BAD_REQUEST.value()
            );
        }

        BatchResponse batch = batchService.createBatch(
               userDetails.getToken(), userDetails.getTenantId(), request.getName(), request.getCreatedBy(), request.getApplicationIds());

        return ResponseEntity.ok(batch);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApplicationBatch> updateBatchStatus(
            @PathVariable UUID id,
            @RequestBody BatchStatus status) {

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!(principal instanceof CustomUserDetails userDetails)) {
            throw new ApplicationException(
                    "User not authenticated can't get the principal in application batch controller",
                    HttpStatus.BAD_REQUEST.value()
            );
        }
        return ResponseEntity.ok(batchService.updateBatchStatus(id, status, userDetails.getTenantId()));
    }

    @GetMapping
    public ResponseEntity<List<ApplicationBatch>> getBatches(){
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!(principal instanceof CustomUserDetails userDetails)) {
            throw new ApplicationException(
                    "User not authenticated can't get the principal in application batch controller",
                    HttpStatus.BAD_REQUEST.value()
            );
        }

        return ResponseEntity.ok(batchService.getBatchesByTenant(userDetails.getTenantId()));
    }
}
