package com.hashjosh.communication.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class AttachmentDto {
    private UUID documentId;
    private String url;
    private String type; // e.g., "image", "file"
}
