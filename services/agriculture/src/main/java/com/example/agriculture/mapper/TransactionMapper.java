package com.example.agriculture.mapper;

import com.example.agriculture.dto.transaction.TransactionRequest;
import com.example.agriculture.dto.transaction.TransactionResponse;
import com.example.agriculture.entity.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TransactionMapper {

    public Transaction toTransactionEntity(TransactionRequest request) {
        return Transaction.builder()
                .name(request.getName())
                .type(request.getType())
                .amount(request.getAmount())
                .status(request.getStatus())
                .isPositive(request.isPositive())
                .date(request.getDate())
                .build();
    }

    public TransactionResponse toTransactionResponse(Transaction transaction) {
        return TransactionResponse.builder()
                .transactionId(transaction.getId())
                .name(transaction.getName())
                .type(transaction.getType())
                .amount(transaction.getAmount())
                .status(transaction.getStatus())
                .isPositive(transaction.isPositive())
                .date(transaction.getDate())
                .build();
    }

    public void updateTransactionFromRequest(Transaction transaction, TransactionRequest request) {
        transaction.setName(request.getName());
        transaction.setType(request.getType());
        transaction.setAmount(request.getAmount());
        transaction.setStatus(request.getStatus());
        transaction.setPositive(request.isPositive());
        if (request.getDate() != null) {
            transaction.setDate(request.getDate());
        }
    }
}
