package com.hashjosh.applicationservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@Table(name = "application_types")
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "layout", length = 255)
    private String layout;

    @Column(name = "name", length = 255)
    private String name;

    @Column(name = "created_at")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    @OneToMany(mappedBy = "applicationType", fetch = FetchType.EAGER , cascade = CascadeType.ALL)
    private List<ApplicationSection> sections;

}