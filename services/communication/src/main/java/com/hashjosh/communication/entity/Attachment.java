package com.hashjosh.communication.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "attachments")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Attachment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String url;

    @ManyToMany(mappedBy = "attachments")
    private Set<Message> messages = new HashSet<>();
}
