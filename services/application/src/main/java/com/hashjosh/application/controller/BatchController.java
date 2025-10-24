package com.hashjosh.application.controller;

import com.hashjosh.application.dto.batch.BatchRequestDTO;
import com.hashjosh.application.dto.batch.BatchResponseDTO;
import com.hashjosh.application.service.BatchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/batches")
@Slf4j
public class BatchController {

    private final BatchService batchService;

    @PostMapping()
    public ResponseEntity<BatchResponseDTO> createBatch(
            @Valid @RequestBody BatchRequestDTO batch
    ){
        // Implementation for creating a new batch would go here
        BatchResponseDTO response = batchService.createBatch(batch);
        return ResponseEntity.ok(response);
    }

    @GetMapping()
    public ResponseEntity<List<BatchResponseDTO>> getAllBatches(){
        log.info("Received request to get all batches");
        List<BatchResponseDTO> response = batchService.getAllBatches();
        log.info("Returning {} batches", response.size());
        return ResponseEntity.ok(response);
    }


    @GetMapping("/application-type/{applicationTypeId}")
    public ResponseEntity<List<BatchResponseDTO>> getBatchesByApplicationType(
            @PathVariable("applicationTypeId") UUID applicationTypeId,
            @RequestParam(required = false) boolean isAvailable
    ){
        // Implementation for getting batches by application type would go here
        List<BatchResponseDTO> response = batchService.getAvailableBatchesByApplicationType(applicationTypeId, isAvailable);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/name/{batchName}")
    public ResponseEntity<BatchResponseDTO> getBatchByName(
            @PathVariable("batchName") String batchName
    ){
        // Implementation for getting a batch by its ID would go here
        BatchResponseDTO response = batchService.getBatchByName(batchName);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/id/{batchId}")
    public ResponseEntity<BatchResponseDTO> getBatchById(
            @PathVariable("batchId") UUID batchId
    ){
        // Implementation for getting a batch by its ID would go here
        BatchResponseDTO response = batchService.getBatchById(batchId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{batchId}")
    public ResponseEntity<BatchResponseDTO> updateBatch(
            @PathVariable("batchId") UUID batchId,
            @RequestBody BatchRequestDTO requestDTO
    ){
        BatchResponseDTO response = batchService.updateBatch(requestDTO, batchId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{batchId}")
    public ResponseEntity<Void> deleteBatch(
            @PathVariable("batchId") UUID batchId
    ) {
        batchService.deleteBatch(batchId);
        return ResponseEntity.noContent().build();
    }


}
