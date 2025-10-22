package com.hashjosh.application.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.hashjosh.constant.application.RecipientType;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "applications")
public class Application implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    @JsonProperty("id")
    private UUID id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_type_id", nullable = false)
    @JsonIgnore
    @JsonProperty("applicationTypeId")
    private ApplicationType applicationType;

    @Column(name = "uploaded_by", nullable = false)
    @JsonProperty("uploadedBy")
    private UUID uploadedBy;

    @JsonProperty("documentsUploaded")
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "application_documents",
            joinColumns = @JoinColumn(name = "application_id"),
            inverseJoinColumns = @JoinColumn(name = "document_id")
    )
    @Column(name = "documents_uploaded", nullable = true)
    private Set<Document> documentsUploaded = new HashSet<>();

    @Type(JsonBinaryType.class)
    @Column(name = "dynamic_fields", columnDefinition = "jsonb", nullable = false)
    @JsonProperty("dynamicFields")
    private JsonNode dynamicFields;

    @CreationTimestamp
    @Column(name = "submitted_at", updatable = false)
    private LocalDateTime submittedAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Version
    @JsonProperty("version")
    private Long version;

}