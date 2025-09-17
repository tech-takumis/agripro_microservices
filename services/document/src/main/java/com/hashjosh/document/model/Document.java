package com.hashjosh.document.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.hashjosh.document.enums.DocumentType;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Table(name = "documents")
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "document_type", nullable = false)
    private DocumentType documentType;

    @Column(name = "reference_id    ", nullable = false)
    private UUID referenceId ; // ID of the entity this document relates to

    @Column(name = "uploaded_by",nullable = false)
    private UUID uploadedBy;

    @Column(name = "file_name", length = 255)
    private String fileName;

    @Column(name = "file_type", nullable = false)
    private String fileType;


    @Column(name = "object_key", length = 255)
    private String objectKey; // Minio object key

    @Type(JsonBinaryType.class)
    @Column(name = "meta_data", columnDefinition = "jsonb")
    private JsonNode metaData;

    @Column(name = "uploaded_at")
    @CreationTimestamp
    private LocalDateTime uploadedAt;

}
