package com.hashjosh.constant.document.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Data
public class DocumentResponse{
    UUID documentId;
    UUID uploadedBy;
    String fileName;
    String fileType;
    Long fileSize;
    String objectKey;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS")
    LocalDateTime uploadedAt;
    String preview;
}
