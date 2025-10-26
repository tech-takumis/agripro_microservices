package com.hashjosh.pcic.controller;


import com.hashjosh.pcic.dto.permission.PermissionRequest;
import com.hashjosh.pcic.dto.permission.PermissionResponse;
import com.hashjosh.pcic.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/pcic/permissions")
public class PermissionController {

    private final PermissionService permissionService;

    @PostMapping
    public ResponseEntity<PermissionResponse> createPermission(@RequestBody PermissionRequest request) {
        return ResponseEntity.ok(permissionService.createPermission(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PermissionResponse> getPermission(@PathVariable UUID id) {
        return ResponseEntity.ok(permissionService.getPermission(id));
    }

    @GetMapping
    public ResponseEntity<List<PermissionResponse>> getAllPermissions() {
        return ResponseEntity.ok(permissionService.getAllPermissions());
    }

    @PutMapping("/{id}")
    public ResponseEntity<PermissionResponse> updatePermission(@PathVariable UUID id, @RequestBody PermissionRequest request) {
        return ResponseEntity.ok(permissionService.updatePermission(id, request));
    }
}
