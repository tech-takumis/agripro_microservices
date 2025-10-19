package com.example.agriculture.controller;

import com.example.agriculture.dto.auth.AgricultureResponseDto;
import com.example.agriculture.service.AgricultureService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/agriculture")
@RequiredArgsConstructor
@Slf4j
public class AgricultureController {

    private final AgricultureService agricultureService;


    @PostMapping("/{userId}/permissions/{permissionId}")
    public ResponseEntity<Void> assignDirectPermission(@PathVariable UUID userId, @PathVariable UUID permissionId) {
        agricultureService.assignDirectPermission(userId, permissionId);
        return ResponseEntity.ok().build();
    }


    @GetMapping
    public ResponseEntity<Page<AgricultureResponseDto>> getAll(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size

    ){

        Pageable pageable = PageRequest.of(page,size, Sort.by("id").descending());
        Page<AgricultureResponseDto> result = agricultureService.getAll(search,pageable);

        return ResponseEntity.ok(result);

    }

    @GetMapping("/{id}")
    public ResponseEntity<AgricultureResponseDto> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(agricultureService.getById(id));
    }

    @GetMapping("/{id}/name")
    public ResponseEntity<String> getAgricultureName(@PathVariable UUID id) {
        String name = agricultureService.getAgricultureName(id);
        return ResponseEntity.ok(name);
    }


    @GetMapping("/{userId}/effective-permissions")
    public ResponseEntity<Set<String>> getEffectivePermissions(@PathVariable UUID userId) {
        Set<String> permissions = agricultureService.getEffectivePermissions(userId);
        return ResponseEntity.ok(permissions);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        agricultureService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
