package com.hashjosh.identity.controller;

import com.hashjosh.identity.dto.DesignatedRequestDTO;
import com.hashjosh.identity.dto.DesignatedResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/designated")
@RequiredArgsConstructor
public class DesignatedController {

    private final DesignatedService designatedService;

    @GetMapping
    public ResponseEntity<List<DesignatedResponseDTO>> getAllDesignated() {
        return ResponseEntity.ok(designatedService.getAllDesignated());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DesignatedResponseDTO> getDesignatedById(@PathVariable UUID id) {
        return ResponseEntity.ok(designatedService.getDesignatedById(id));
    }

    @PostMapping
    public ResponseEntity<DesignatedResponseDTO> createDesignated(@RequestBody DesignatedRequestDTO requestDTO) {
        return new ResponseEntity<>(designatedService.createDesignated(requestDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DesignatedResponseDTO> updateDesignated(
            @PathVariable UUID id,
            @RequestBody DesignatedRequestDTO requestDTO) {
        return ResponseEntity.ok(designatedService.updateDesignated(id, requestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDesignated(@PathVariable UUID id) {
        designatedService.deleteDesignated(id);
        return ResponseEntity.noContent().build();
    }
}