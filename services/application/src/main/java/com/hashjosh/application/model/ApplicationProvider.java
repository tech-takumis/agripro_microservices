package com.hashjosh.application.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "application_providers")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationProvider {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String name;
    private String description;
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "provider", fetch = FetchType.EAGER)
    private List<ApplicationType> applicationTypes;
}
