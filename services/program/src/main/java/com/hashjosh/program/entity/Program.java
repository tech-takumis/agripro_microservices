package com.hashjosh.program.entity;

import com.fasterxml.jackson.databind.JsonNode;
import com.hashjosh.constant.program.enums.ProgramStatus;
import com.hashjosh.constant.program.enums.ProgramType;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Table(name = "programs")
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class Program {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 100, name = "name")
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "type")
    private ProgramType type;

    @Column(nullable = false,name = "status")
    @Enumerated(EnumType.STRING)
    private ProgramStatus status;

    @Column(nullable = false, name = "completion")
    private int completion;

    @Column(name = "extra_fields", nullable = false, columnDefinition = "jsonb")
    @Type(JsonBinaryType.class)
    private JsonNode extraFields;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
