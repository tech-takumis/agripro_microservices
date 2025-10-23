package com.example.agriculture.service;

import com.example.agriculture.dto.transaction.TransactionRequest;
import com.example.agriculture.dto.transaction.TransactionResponse;
import com.example.agriculture.entity.Transaction;
import com.example.agriculture.exception.ApiException;
import com.example.agriculture.mapper.TransactionMapper;
import com.example.agriculture.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;

    @Transactional
    public TransactionResponse createTransaction(TransactionRequest request) {
        log.info("Creating new transaction: {}", request.getName());

        Transaction transaction = transactionMapper.toTransactionEntity(request);
        // Set current date if not provided
        if (transaction.getDate() == null) {
            transaction.setDate(LocalDateTime.now());
        }

        return transactionMapper.toTransactionResponse(transactionRepository.save(transaction));
    }

    @Transactional(readOnly = true)
    public TransactionResponse getTransaction(UUID id) {
        return transactionMapper.toTransactionResponse(findTransactionById(id));
    }

    @Transactional(readOnly = true)
    public Page<TransactionResponse> getAllTransactions(Pageable pageable) {
        return transactionRepository.findAll(pageable)
                .map(transactionMapper::toTransactionResponse);
    }

    @Transactional
    public TransactionResponse updateTransaction(UUID id, TransactionRequest request) {
        log.info("Updating transaction with ID: {}", id);

        Transaction transaction = findTransactionById(id);
        transactionMapper.updateTransactionFromRequest(transaction, request);

        return transactionMapper.toTransactionResponse(transactionRepository.save(transaction));
    }

    @Transactional
    public void deleteTransaction(UUID id) {
        log.info("Deleting transaction with ID: {}", id);
        Transaction transaction = findTransactionById(id);
        transactionRepository.delete(transaction);
    }

    @Transactional(readOnly = true)
    public Page<TransactionResponse> findTransactionsByType(String type, Pageable pageable) {
        return transactionRepository.findByType(type, pageable)
                .map(transactionMapper::toTransactionResponse);
    }

    @Transactional(readOnly = true)
    public Page<TransactionResponse> findTransactionsByDateRange(
            LocalDateTime startDate,
            LocalDateTime endDate,
            Pageable pageable) {
        return transactionRepository.findByDateBetween(startDate, endDate, pageable)
                .map(transactionMapper::toTransactionResponse);
    }

    private Transaction findTransactionById(UUID id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("Transaction with ID " + id + " not found"));
    }
}
