package com.hashjosh.userservicev2.controller;


import com.hashjosh.userservicev2.dto.AuthorityDto;
import com.hashjosh.userservicev2.dto.AuthorityResponseDto;
import com.hashjosh.userservicev2.services.AuthorityService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/authorities")
public class AuthorityController {

    private final AuthorityService service;

    public AuthorityController(AuthorityService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<AuthorityResponseDto>> getAuthorities() {
        return new ResponseEntity<>(service.findAll(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<List<AuthorityResponseDto>> createAuthorities(
            @RequestBody List<AuthorityDto> authorities) {
        return new ResponseEntity<>(service.saveAuthorities(authorities),HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAuthority(@PathVariable Long id) {
        service.delete(id);

        return ResponseEntity.noContent().build();
    }
}
