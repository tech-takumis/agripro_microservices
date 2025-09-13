package com.hashjosh.application.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hashjosh.application.dto.ApplicationResponseDto;
import com.hashjosh.application.dto.ApplicationSubmissionDto;
import com.hashjosh.application.dto.ValidationErrors;
import com.hashjosh.application.service.ApplicationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;
    private final ObjectMapper objectMapper;

    @PostMapping(
            value = "/{application-type-id}/submit",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<?> submitApplication(
            @PathVariable("application-type-id") UUID applicationTypeId,
            @RequestPart("fieldValues") JsonNode fieldValues,
            @RequestPart(required = false) Map<String, MultipartFile> files, // use map keyed by field keys
            HttpServletRequest request
    ) {
        try {
            log.info("Content type submitted: {}", request.getContentType());

            if (applicationTypeId == null || applicationTypeId.toString().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid application ID"));
            }


            // Wrap into DTO
            ApplicationSubmissionDto submission = new ApplicationSubmissionDto(fieldValues,
                    files != null ? files : Map.of());

            List<ValidationErrors> errors =
                    applicationService.submitApplication(submission, applicationTypeId, request);

            if (!errors.isEmpty()) {
                return ResponseEntity.badRequest().body(errors);
            }

            return ResponseEntity.ok(Map.of(
                    "message", "Application submitted successfully",
                    "applicationId", applicationTypeId
            ));

        } catch (FileUploadException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "File upload failed: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An unexpected error occurred: " + e.getMessage()));
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

    @PutMapping("/{application-id}w ")
    public ResponseEntity<ApplicationResponseDto> verifiedApplicationStatus(
            @PathVariable("application-id") UUID applicationId,
            @RequestParam String status
    ){
        return new ResponseEntity<>(applicationService.verifiedApplicationStatus(applicationId,status),HttpStatus.CREATED);
    }

    @PostMapping("/test/file-upload")
    public ResponseEntity<String> testFileUpload(
            @RequestPart MultipartFile file,
            HttpServletRequest request
    ) throws IOException {
        log.info("File uploaded successfully {}", file.getOriginalFilename());
        return new ResponseEntity<>(applicationService.testFileUpload(file,request), HttpStatus.OK);
    }
}
