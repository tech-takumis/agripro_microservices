package com.hashjosh.applicationservice.dto;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record ApplicationSubmissionDto(
         JsonNode fieldValues,
        List<MultipartFile> files
        ) {
}
