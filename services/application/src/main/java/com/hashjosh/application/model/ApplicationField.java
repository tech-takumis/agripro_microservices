package com.hashjosh.application.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.hashjosh.application.enums.FieldType;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "application_field")
public class ApplicationField {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "choices", columnDefinition = "jsonb")
    @Type(JsonBinaryType.class)
    private JsonNode choices;

    @Column(name = "default_value", length = 255)
    private String defaultValue;

    @Column(name = "key", length = 255, nullable = false)
    private String key;

    @Column(name = "field_name", length = 255)
    private String fieldName;

    @Column(name = "field_type", length = 255)
    @Enumerated(EnumType.STRING)
    private FieldType fieldType;

    @Column(name = "required")
    private Boolean required;

    @Column(name = "validation_regex", length = 255)
    private String validationRegex;

    @ManyToOne
    @JoinColumn(name = "application_section_id")
    private ApplicationSection applicationSection;

    @Column(name = "created_at")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();


}