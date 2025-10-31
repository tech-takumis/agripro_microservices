package com.hashjosh.application.dto.document;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentRequest {

    private UUID documentId;
    private String fileName;
    private String fileType;
    private String coordinates;
}
