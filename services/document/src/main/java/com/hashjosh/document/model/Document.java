package com.hashjosh.document.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.hashjosh.constant.program.enums.DocumentType;
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


    @Column(name = "uploaded_by",nullable = false)
    private UUID uploadedBy;

    @Column(name = "file_name", length = 255)
    private String fileName;

    @Column(name = "file_type", nullable = false)
    private String fileType;

    @Column(name = "object_key", length = 255)
    private String objectKey; // Minio object key

    @Column(name = "uploaded_at")
    @CreationTimestamp
    private LocalDateTime uploadedAt;

}
