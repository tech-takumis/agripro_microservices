package com.hashjosh.verification.controller;

import com.hashjosh.verification.dto.BatchRequestDTO;
import com.hashjosh.verification.dto.BatchResponseDTO;
import com.hashjosh.verification.service.BatchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/batch")
@RequiredArgsConstructor
@Slf4j
public class BatchController {

    private final BatchService batchService;


    @PostMapping
    public ResponseEntity<String> createBatch(
            @RequestBody @Valid BatchRequestDTO request
    ) {
        log.info("Creating a new batch");
        BatchResponseDTO response = batchService.createBatch(request);
        return ResponseEntity.ok("Batch created with ID: " + response.getId());
    }

    @GetMapping
    public ResponseEntity<List<BatchResponseDTO>> getAllBatches() {
        log.info("Fetching all batches");
        List<BatchResponseDTO> batches = batchService.getAllBatches();
        return ResponseEntity.ok(batches);
    }
}
