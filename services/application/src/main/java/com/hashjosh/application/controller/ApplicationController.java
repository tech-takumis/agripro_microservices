package com.hashjosh.application.controller;

import com.hashjosh.application.dto.ApplicationResponseDto;
import com.hashjosh.application.dto.ApplicationSubmissionDto;
import com.hashjosh.application.dto.ApplicationSubmissionResponse;
import com.hashjosh.application.model.Application;
import com.hashjosh.application.service.ApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    @PostMapping(value = "/submit")
    public ResponseEntity<ApplicationSubmissionResponse> submitApplication(
            @Valid @RequestBody ApplicationSubmissionDto submission
    ) {
        Application application = applicationService.processSubmission(submission);
        return ResponseEntity.ok(ApplicationSubmissionResponse.builder()
                        .applicationId(application.getId())
                        .success(true)
                        .message("Application submitted successfully")
                        .build());
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

    @DeleteMapping("/{application-id}")
    public ResponseEntity<Void> deleteApplication(
            @PathVariable("application-id") UUID applicationId
    ){
        applicationService.deleteApplication(applicationId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
