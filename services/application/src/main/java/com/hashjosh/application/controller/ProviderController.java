package com.hashjosh.application.controller;

import com.hashjosh.application.dto.provider.ProviderRequest;
import com.hashjosh.application.dto.provider.ProviderResponseDTO;
import com.hashjosh.application.service.ProviderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/providers")
@RequiredArgsConstructor
public class ProviderController {
    private final ProviderService providerService;

    @PostMapping
    public ResponseEntity<String> createProvider(
            @RequestBody @Valid ProviderRequest request
    ){
        providerService.createProvider(request);
        return ResponseEntity.ok("Provider created successfully");
    }

    @GetMapping
    public ResponseEntity<List<ProviderResponseDTO>> findAllProviders(){
        List<ProviderResponseDTO> providers = providerService.getAllProviders();
        return ResponseEntity.ok(providers);
    }

    @GetMapping("/{provider-name}")
    public ResponseEntity<ProviderResponseDTO> getProviderByName(
            @PathVariable("provider-name") String providerName
    ){

        ProviderResponseDTO response = providerService.getProviderByName(providerName);

        return ResponseEntity.ok(response);
    }


    @PutMapping("/{provider-name}")
    public ResponseEntity<String> updateProvider(
            @PathVariable("provider-name") String providerName,
            @RequestBody @Valid ProviderRequest request
    ){
        providerService.updateProvider(providerName, request);
        return ResponseEntity.ok("Provider updated successfully");
    }

    @DeleteMapping
    public ResponseEntity<String> deleteProvider(
            @RequestParam("provider-name") String providerName
    ){
        providerService.deleteProvider(providerName);
        return ResponseEntity.ok("Provider deleted successfully");
    }
}
