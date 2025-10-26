package com.hashjosh.application.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@Table(name = "application_types")
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationType {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "layout", length = 255)
    private String layout;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "provider_id", referencedColumnName = "id")
    private ApplicationProvider provider;

    @Column(name = "name", length = 255)
    private String name;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt = LocalDateTime.now();

    @OneToMany(mappedBy = "applicationType", fetch = FetchType.EAGER , cascade = CascadeType.ALL)
    private List<ApplicationSection> sections;

    @OneToMany(mappedBy = "applicationType", fetch = FetchType.EAGER)
    private List<Batch> batches;

}