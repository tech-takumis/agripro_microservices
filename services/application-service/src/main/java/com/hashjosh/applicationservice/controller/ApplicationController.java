package com.hashjosh.applicationservice.controller;

import com.hashjosh.applicationservice.dto.ValidationErrors;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hashjosh.applicationservice.dto.ApplicationSubmissionDto;
import com.hashjosh.applicationservice.service.ApplicationService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;
    private final ObjectMapper objectMapper;

    @PostMapping(value = "/{application-id}/submit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> submitApplication(
            @PathVariable("application-id") Long applicationId,
            @RequestPart("fieldValues")  String fieldValues,
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            HttpSession session) throws FileUploadException {
        try {
            if (applicationId == null || applicationId <= 0) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid application ID"));
            }

            // Convert String fieldValue to JsonNode
            JsonNode jsonNode;
            try{
                jsonNode = objectMapper.readTree(fieldValues);
            }catch (Exception e){
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid JSON in fieldValues: " + e.getMessage()));
            }
            ApplicationSubmissionDto submission = new ApplicationSubmissionDto(jsonNode, files != null ? files : List.of());
            List<ValidationErrors> errors = applicationService.submitApplication(submission, applicationId, session);

            if (!errors.isEmpty()) {
                return ResponseEntity.badRequest().body(errors);
            }
            return ResponseEntity.ok(Map.of("message", "Application submitted successfully", "applicationId", applicationId));
        } catch (FileUploadException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "File upload failed: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An unexpected error occurred: " + e.getMessage()));
        }
    }
}
