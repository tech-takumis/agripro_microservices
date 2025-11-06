package com.hashjosh.communication.entity;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "posts")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @JoinColumn(name = "author_id", nullable = false)
    private UUID authorId;

    @Column(name = "content", nullable = false, length = 999)
    private String content;

    @ElementCollection
    @CollectionTable(
            name = "post_documents",
            joinColumns = @JoinColumn(name = "post_id")
    )
    @Column(name = "document_id")
    private List<UUID> documentIds = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

}
