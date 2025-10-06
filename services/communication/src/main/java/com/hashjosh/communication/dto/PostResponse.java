package com.hashjosh.communication.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class PostResponse {
    private UUID id;
    private String title;
    private String content;
    private UUID authorId;
    private List<UUID> documentIds;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
