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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

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
            @Valid @RequestBody ApplicationSubmissionDto submission,
            HttpServletRequest request) {

        try {

            // Get the current user from security context
            CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext()
                    .getAuthentication()
                    .getPrincipal();

            // Process the submission
            ApplicationSubmissionResponse response = applicationService.processSubmission(
                    submission,
                    userDetails
            );

            // Return appropriate response
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApplicationSubmissionResponse.builder()
                            .success(false)
                            .message("An error occurred while processing your application: " + e.getMessage())
                            .build());
        }
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
}
