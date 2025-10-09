package com.example.agriculture.controller;

import com.example.agriculture.dto.AgricultureResponseDto;
import com.example.agriculture.service.AgricultureService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/agriculture")
@RequiredArgsConstructor
@Slf4j
public class AgricultureController {

    private final AgricultureService agricultureService;

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

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        agricultureService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
