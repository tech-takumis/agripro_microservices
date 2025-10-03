package com.example.agriculture.controller;


import com.example.agriculture.dto.BatchResponse;
import com.example.agriculture.service.BatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/batches")
public class BatchController {

    private final BatchService batchService;

    @GetMapping("/open")
    public ResponseEntity<BatchResponse> getOpenBatch() {
        try {
            BatchResponse response = batchService.findOpenNonExpiredBatch();
            return ResponseEntity.ok(response);
        } catch (IllegalStateException e) {
            return ResponseEntity.notFound().build();
        }
    }


}
