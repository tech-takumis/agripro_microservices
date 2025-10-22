package com.hashjosh.application.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hashjosh.application.configs.CustomUserDetails;
import com.hashjosh.application.dto.ApplicationResponseDto;
import com.hashjosh.application.dto.ApplicationSubmissionDto;
import com.hashjosh.application.dto.ApplicationSubmissionResponse;
import com.hashjosh.application.service.ApplicationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    @PostMapping("/submit")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApplicationSubmissionResponse> submitApplication(
            @Valid @RequestPart ApplicationSubmissionDto submission,
            @RequestPart(required = false) List<MultipartFile> files
    ) {
        try {
            log.debug("Processing application submission for type: {}", submission.getApplicationTypeId());
            ApplicationSubmissionResponse response = applicationService.processSubmission(
                    submission,
                    files != null ? files : Collections.emptyList()
            );

            // If success, return the same values as the submission DTO (plus success/message)
            if (response.isSuccess()) {
                ApplicationSubmissionResponse successResponse = ApplicationSubmissionResponse.builder()
                        .success(true)
                        .message("Application submitted successfully")
                        .applicationId(response.getApplicationId())
                        .applicationTypeId(submission.getApplicationTypeId())
                        .uploadedBy(submission.getUploadedBy())
                        .fieldValues(submission.getFieldValues())
                        .build();
                return ResponseEntity.ok(successResponse);
            } else {
                return ResponseEntity.badRequest().body(response);
            }

        } catch (ResourceNotFoundException e) {
            log.error("Resource not found during application submission", e);
            return buildErrorResponse(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            log.error("Error processing application submission", e);
            return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                    "An error occurred while processing your application");
        }
    }

    private ResponseEntity<ApplicationSubmissionResponse> buildErrorResponse(
            HttpStatus status, String message) {
        return ResponseEntity
                .status(status)
                .body(ApplicationSubmissionResponse.builder()
                        .success(false)
                        .message(message)
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
        return new ResponseEntity<>(applicationService.findApplicationByType(applicationTypeId), HttpStatus.OK);
    }

    @GetMapping("/agriculture")
    public ResponseEntity<List<ApplicationResponseDto>> findAllAgricultureApplication(){
            return new ResponseEntity<>(applicationService.findAllAgricultureApplication(),HttpStatus.OK);
    }
}
