package com.hashjosh.transaction.service;

import com.hashjosh.transaction.dto.TransactionRequestDTO;
import com.hashjosh.transaction.dto.TransactionResponseDTO;
import com.hashjosh.transaction.entity.Transaction;
import com.hashjosh.transaction.exception.ApiException;
import com.hashjosh.transaction.mapper.TransactionMapper;
import com.hashjosh.transaction.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;

    public List<TransactionResponseDTO> getAllTransactions() {
        return transactionRepository.findAll().stream()
                .map(transactionMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public TransactionResponseDTO getTransactionById(UUID id) {
        return transactionRepository.findById(id)
                .map(transactionMapper::toResponseDto)
                .orElseThrow(() -> ApiException.notFound("Transaction not found with id: " + id));
    }

    @Transactional
    public TransactionResponseDTO createTransaction(TransactionRequestDTO requestDTO) {
        validateTransaction(requestDTO);
        Transaction transaction = transactionMapper.toEntity(requestDTO);
        transaction = transactionRepository.save(transaction);
        return transactionMapper.toResponseDto(transaction);
    }

    @Transactional
    public TransactionResponseDTO updateTransaction(UUID id, TransactionRequestDTO requestDTO) {
        validateTransaction(requestDTO);
        Transaction existingTransaction = transactionRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("Transaction not found with id: " + id));

        Transaction updatedTransaction = transactionMapper.toEntity(requestDTO);
        updatedTransaction.setId(id);
        updatedTransaction = transactionRepository.save(updatedTransaction);
        return transactionMapper.toResponseDto(updatedTransaction);
    }

    @Transactional
    public void deleteTransaction(UUID id) {
        if (!transactionRepository.existsById(id)) {
            throw ApiException.notFound("Transaction not found with id: " + id);
        }
        transactionRepository.deleteById(id);
    }

    private void validateTransaction(TransactionRequestDTO requestDTO) {
        if (requestDTO.getName() == null || requestDTO.getName().trim().isEmpty()) {
            throw ApiException.badRequest("Transaction name is required");
        }
        if (requestDTO.getAmount() <= 0) {
            throw ApiException.badRequest("Transaction amount must be greater than 0");
        }
    }
}