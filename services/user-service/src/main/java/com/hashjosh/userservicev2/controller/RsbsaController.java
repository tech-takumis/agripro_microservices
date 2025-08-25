package com.hashjosh.userservicev2.controller;


import com.hashjosh.userservicev2.dto.RsbsaRequestDto;
import com.hashjosh.userservicev2.dto.RsbsaResponseDto;
import com.hashjosh.userservicev2.services.RsbsaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rsbsa")
public class RsbsaController {

    private final RsbsaService rsbsaService;

    public RsbsaController(RsbsaService rsbsaService) {
        this.rsbsaService = rsbsaService;
    }

    @PostMapping()
    public ResponseEntity<RsbsaResponseDto> save(@RequestBody RsbsaRequestDto dto){
        return new ResponseEntity<>(rsbsaService.save(dto),HttpStatus.CREATED);
    }

    @GetMapping()
    public ResponseEntity<List<RsbsaResponseDto>> findAll(){
        return new ResponseEntity<>(rsbsaService.findAll(),HttpStatus.FOUND);
    }
}
