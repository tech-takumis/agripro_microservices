package com.hashjosh.document.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record DocumentResponse(
        UUID documentId,
        UUID referenceId,
        UUID uploadedBy,
        String fileName,
        String fileType,
        Long fileSize,
        String objectKey,
        JsonNode metaData,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS")
        LocalDateTime uploadedAt,
        String downloadUrl
) {
    public record DownloadFile(
            String fileName,
            String fileType,
            byte[] fileData
    ) {
        public String getContentDisposition() {
            return "attachment; filename=\"" + fileName + "\"";
        }
    }
}
