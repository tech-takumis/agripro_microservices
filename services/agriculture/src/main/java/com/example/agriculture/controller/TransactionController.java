package com.example.agriculture.controller;


import com.example.agriculture.dto.transaction.TransactionRequest;
import com.example.agriculture.dto.transaction.TransactionResponse;
import com.example.agriculture.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/agriculture/transactions")
public class TransactionController {
    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<TransactionResponse> createTransaction(
            @RequestBody @Valid TransactionRequest request) {
        return ResponseEntity.ok(transactionService.createTransaction(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponse> getTransaction(@PathVariable UUID id) {
        return ResponseEntity.ok(transactionService.getTransaction(id));
    }

    @GetMapping
    public ResponseEntity<Page<TransactionResponse>> getAllTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("date").descending());
        return ResponseEntity.ok(transactionService.getAllTransactions(pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransactionResponse> updateTransaction(
            @PathVariable UUID id,
            @RequestBody @Valid TransactionRequest request) {
        return ResponseEntity.ok(transactionService.updateTransaction(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable UUID id) {
        transactionService.deleteTransaction(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<Page<TransactionResponse>> getTransactionsByType(
            @PathVariable String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("date").descending());
        return ResponseEntity.ok(transactionService.findTransactionsByType(type, pageable));
    }

    @GetMapping("/date-range")
    public ResponseEntity<Page<TransactionResponse>> getTransactionsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("date").descending());
        return ResponseEntity.ok(transactionService.findTransactionsByDateRange(startDate, endDate, pageable));
    }
}
