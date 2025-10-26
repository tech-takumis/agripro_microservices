package com.hashjosh.application.controller;

import com.hashjosh.constant.application.ApplicationResponseDto;
import com.hashjosh.application.dto.submission.ApplicationSubmissionDto;
import com.hashjosh.application.dto.submission.ApplicationSubmissionResponse;
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

    // Return all applications by provider name
    @GetMapping("/provider/{provider}")
    public ResponseEntity<List<ApplicationResponseDto>> findAllProviderApplication(
            @PathVariable("provider") String provider
    ){
        return new ResponseEntity<>(applicationService.findAllProviderApplication(provider),HttpStatus.OK);
    }

    // We get a specific application by its id
    @GetMapping("/{application-id}")
    public ResponseEntity<ApplicationResponseDto> findById(
            @PathVariable("application-id") UUID applicationId
    ){
        return  ResponseEntity.ok(applicationService.getApplicationById(applicationId));
    }
    // We get all applications -  for the admin side
    @GetMapping
    public ResponseEntity<List<ApplicationResponseDto>> findAll(){
        return  new ResponseEntity<>(applicationService.findAll(),HttpStatus.OK);
    }

    // We get all applications by batch name
    @GetMapping("/batch/{batchName}")
    public ResponseEntity<List<ApplicationResponseDto>> findAllApplicationByBatchName(
            @PathVariable("batchName") String batchName
    ){
        return new ResponseEntity<>(applicationService.findAllApplicationByBatchName(batchName),HttpStatus.OK);
    }

    // we get all applications by batch id
    @GetMapping("/batch/id/{batchId}")
    public ResponseEntity<List<ApplicationResponseDto>> findAllApplicationByBatchId(
            @PathVariable("batchId") UUID batchId
    ){
        return new ResponseEntity<>(applicationService.findAllApplicationByBatchId(batchId),HttpStatus.OK);
    }


    @DeleteMapping("/{application-id}")
    public ResponseEntity<Void> deleteApplication(
            @PathVariable("application-id") UUID applicationId
    ){
        applicationService.deleteApplication(applicationId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
