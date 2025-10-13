package com.example.agriculture.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;
    private String type;
    private float amount;
    private String status;
    private boolean isPositive;
    private LocalDateTime date;

    @PrePersist
    protected void onCreate() {
        if (date == null) {
            date = LocalDateTime.now();
        }
    }
}
