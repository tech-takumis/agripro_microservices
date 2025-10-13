package com.example.agriculture.dto.transaction;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionResponse {
    private UUID transactionId;
    private String name;
    private String type;
    private float amount;
    private String status;
    private boolean isPositive;
    private LocalDateTime date;
}
