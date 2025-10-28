package com.hashjosh.farmer.controller;

import com.hashjosh.constant.farmer.FarmerReponse;
import com.hashjosh.farmer.service.FarmerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/farmer")
public class FarmerController {

    private final FarmerService farmerService;

    @GetMapping("/{farmer-id}")
    public ResponseEntity<FarmerReponse> getFarmerById(
            @PathVariable("farmer-id") UUID farmerId
    ) {
        return ResponseEntity.ok(farmerService.findById(farmerId));
    }

    @GetMapping
    public ResponseEntity<List<FarmerReponse>> getAllFarmers() {
        return ResponseEntity.ok(farmerService.findAll());
    }
}
