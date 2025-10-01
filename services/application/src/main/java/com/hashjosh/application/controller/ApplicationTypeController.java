package com.hashjosh.application.controller;

import com.hashjosh.application.dto.ApplicationTypeRequestDto;
import com.hashjosh.application.dto.ApplicationTypeResponseDto;
import com.hashjosh.application.service.ApplicationTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/application/types")
@RequiredArgsConstructor
public class ApplicationTypeController {

    private final ApplicationTypeService applicationTypeService;

    @PostMapping()
    public ResponseEntity<ApplicationTypeResponseDto> create(
            @RequestBody ApplicationTypeRequestDto dto
    ){
        ApplicationTypeResponseDto responseDto = applicationTypeService.create(dto);

        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @GetMapping()
    public ResponseEntity<List<ApplicationTypeResponseDto>> getAll(){
        List<ApplicationTypeResponseDto> allApplicationType = applicationTypeService.findAll();
        return new ResponseEntity<>(allApplicationType,HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApplicationTypeResponseDto> getById(@PathVariable UUID id){
       return new ResponseEntity<>(applicationTypeService.findById(id),HttpStatus.OK);
    }
}

