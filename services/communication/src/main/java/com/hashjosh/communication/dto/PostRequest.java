package com.hashjosh.communication.dto;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class PostRequest {
    private String content;
    private UUID authorId;
    private List<UUID> documentIds;

}
