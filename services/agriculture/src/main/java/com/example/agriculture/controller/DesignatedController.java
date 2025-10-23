package com.example.agriculture.controller;

import com.example.agriculture.dto.DesignatedRequest;
import com.example.agriculture.dto.DesignatedResponse;
import com.example.agriculture.service.DesignatedService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/agriculture/designated")
public class DesignatedController {

    private final DesignatedService designatedService;

    @PostMapping("/assign")
    public ResponseEntity<DesignatedResponse> assignDesignated(
         @RequestBody @Valid DesignatedRequest request
    ) {
        DesignatedResponse response = designatedService.assignDesignated(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{username}")
    public ResponseEntity<DesignatedResponse> getDesignatedByUsername(
            @PathVariable  String username) {
        return ResponseEntity.ok(designatedService.getDesignatedByUsername(username));
    }

    @GetMapping("/type/{serviceType}")
    public ResponseEntity<DesignatedResponse> getDesignatedByServiceType(
            @PathVariable  String serviceType) {
        return ResponseEntity.ok(designatedService.getDesignatedByServiceType(serviceType));
    }

    @GetMapping
    public ResponseEntity<List<DesignatedResponse>> getAllDesignated() {
        return ResponseEntity.ok(designatedService.findAll());
    }

}
