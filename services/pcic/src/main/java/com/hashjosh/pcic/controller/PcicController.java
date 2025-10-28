package com.hashjosh.pcic.controller;


import com.hashjosh.pcic.dto.user.PcicResponse;
import com.hashjosh.pcic.service.PcicService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RequestMapping("/api/v1/pcic")
@RestController
@RequiredArgsConstructor
@Slf4j
public class PcicController {

    private final PcicService pcicService;

    @GetMapping()
    public ResponseEntity<List<PcicResponse>> findAll(){
        return ResponseEntity.ok(pcicService.findAll());
    }

    @GetMapping("/{pcic-id}")
    public ResponseEntity<PcicResponse> findById(
            @PathVariable("pcic-id")UUID pcicId
            ){
        return ResponseEntity.ok(pcicService.findById(pcicId));
    }
}
