package com.hashjosh.application.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@Table(name = "batches")
@AllArgsConstructor
@NoArgsConstructor
public class Batch {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "name", length = 255)
    private String name;
    private String description;
    private boolean isAvailable;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @Column(name = "max_applications", columnDefinition = "int default 10")
    private int maxApplications;

    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "application_type_id", referencedColumnName = "id")
    private ApplicationType applicationType;

    @OneToMany(mappedBy = "batch", fetch = FetchType.EAGER)
    private List<Application> applications;

}
