package com.hashjosh.transaction.mapper;

import com.hashjosh.transaction.dto.TransactionRequestDTO;
import com.hashjosh.transaction.dto.TransactionResponseDTO;
import com.hashjosh.transaction.entity.Transaction;
import org.springframework.stereotype.Component;

@Component
public class TransactionMapper {
    
    public Transaction toEntity(TransactionRequestDTO dto) {
        return Transaction.builder()
                .name(dto.getName())
                .type(dto.getType())
                .amount(dto.getAmount())
                .status(dto.getStatus())
                .isPositive(dto.isPositive())
                .date(dto.getDate())
                .build();
    }

    public TransactionResponseDTO toResponseDto(Transaction entity) {
        return TransactionResponseDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .type(entity.getType())
                .amount(entity.getAmount())
                .status(entity.getStatus())
                .isPositive(entity.isPositive())
                .date(entity.getDate())
                .build();
    }
}