package com.hashjosh.application.controller;

import com.hashjosh.application.dto.ApplicationResponseDto;
import com.hashjosh.application.dto.ApplicationSubmissionDto;
import com.hashjosh.application.service.ApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    @PostMapping(value = "/submit", consumes = {"multipart/form-data"})
    public ResponseEntity<String> submitApplication(
            @Valid @RequestPart ApplicationSubmissionDto submission,
            @RequestPart(value = "files",required = false) List<MultipartFile> files
    ) {
        applicationService.processSubmission(submission, files);
        return ResponseEntity.ok("Application submitted successfully");
    }


    @GetMapping("/{application-id}")
    public ResponseEntity<ApplicationResponseDto> findById(
            @PathVariable("application-id") UUID applicationId
    ){
        return  ResponseEntity.ok(applicationService.getApplicationById(applicationId));
    }

    @GetMapping
    public ResponseEntity<List<ApplicationResponseDto>> findAll(){
        return  new ResponseEntity<>(applicationService.findAll(),HttpStatus.OK);
    }

    @GetMapping("/type/{application_type_id}")
    public ResponseEntity<List<ApplicationResponseDto>> findApplicationByType(
            @PathVariable("application_type_id") UUID applicationTypeId
     ){
        return new ResponseEntity<>(applicationService.findApplicationbyType(applicationTypeId), HttpStatus.OK);
    }

    @GetMapping("/agriculture")
    public ResponseEntity<List<ApplicationResponseDto>> findAllAgricultureApplication(){
            return new ResponseEntity<>(applicationService.findAllAgricultureApplication(),HttpStatus.OK);
    }
}
