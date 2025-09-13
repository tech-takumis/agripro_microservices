package com.hashjosh.application.controller;

import com.hashjosh.application.dto.ApplicationTypeRequestDto;
import com.hashjosh.application.dto.ApplicationTypeResponseDto;
import com.hashjosh.application.dto.SuccessResponse;
import com.hashjosh.application.service.ApplicationTypeService;
import com.hashjosh.application.utils.ResponseUtils;
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
    private final ResponseUtils responseUtils;

    @PostMapping()
    public ResponseEntity<SuccessResponse> create(
            @RequestBody ApplicationTypeRequestDto dto
    ){
        ApplicationTypeResponseDto responseDto = applicationTypeService.create(dto);
        SuccessResponse successResponse = responseUtils.getSuccessResponse(
                HttpStatus.CREATED.value(),
                "Application created successfully",
                responseDto
                );

        return new ResponseEntity<>(successResponse, HttpStatus.CREATED);
    }

    @GetMapping()
    public ResponseEntity<SuccessResponse> getAll(){
        List<ApplicationTypeResponseDto> allApplicationType = applicationTypeService.findAll();
        SuccessResponse successResponse = responseUtils.getSuccessResponse(
                HttpStatus.OK.value(),
                "These are all the application type",
                allApplicationType
        );
        return new ResponseEntity<>(successResponse,HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApplicationTypeResponseDto> getById(@PathVariable UUID id){
       return new ResponseEntity<>(applicationTypeService.findById(id),HttpStatus.OK);
    }
}

