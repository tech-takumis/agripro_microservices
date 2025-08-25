package com.hashjosh.applicationservice.controller;

import com.hashjosh.applicationservice.dto.ApplicationTypeRequestDto;
import com.hashjosh.applicationservice.dto.ApplicationTypeResponseDto;
import com.hashjosh.applicationservice.dto.SuccessResponse;
import com.hashjosh.applicationservice.service.ApplicationTypeService;
import com.hashjosh.applicationservice.utils.ResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/application/types")
@RequiredArgsConstructor
public class ApplicationTypeController {

    private final ApplicationTypeService applicationTypeService;
    private final ResponseUtils responseUtils;

//    TODO - All application rest controller implementation

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
        List<ApplicationTypeResponseDto> allAplicationType = applicationTypeService.findAll();
        SuccessResponse successResponse = responseUtils.getSuccessResponse(
                HttpStatus.OK.value(),
                "These are all the application type",
                allAplicationType
        );
        return new ResponseEntity<>(successResponse,HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SuccessResponse> getById(@PathVariable Long id){
        ApplicationTypeResponseDto applicationTypeResponseDto = applicationTypeService.findById(id);
        SuccessResponse successResponse = responseUtils.getSuccessResponse(
                HttpStatus.OK.value(),
                "Application found successfully",
                applicationTypeResponseDto
        );
        return new ResponseEntity<>(successResponse,HttpStatus.OK);

    }
}

