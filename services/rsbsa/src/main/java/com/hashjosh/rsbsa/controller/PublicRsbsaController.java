package com.hashjosh.rsbsa.controller;

import com.hashjosh.rsbsa.dto.RsbsaResponseDto;
import com.hashjosh.rsbsa.service.RsbsaService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/public/rsbsa")
@RequiredArgsConstructor
public class PublicRsbsaController {

    private final RsbsaService rsbsaService;

    @GetMapping("/{rsbsa-id}")
    public ResponseEntity<RsbsaResponseDto> findByRsbsaId(
            @PathVariable("rsbsa-id") String rsbaId,
            HttpServletRequest request
    ){
        return new ResponseEntity<>(rsbsaService.findByRsbsaId(rsbaId,request), HttpStatus.OK);
    };

}
