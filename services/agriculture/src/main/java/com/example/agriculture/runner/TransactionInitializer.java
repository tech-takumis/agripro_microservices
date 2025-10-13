package com.example.agriculture.runner;

import com.example.agriculture.entity.Transaction;
import com.example.agriculture.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Order(2)
@Slf4j
public class TransactionInitializer implements CommandLineRunner {

    private final TransactionRepository transactionRepository;

    @Override
    public void run(String... args) throws Exception {
        try {
            if (transactionIsNull()) {
                log.info("No transactions found in the database. Initializing transaction sample data...");

                List<Transaction> defaultTransactions = List.of(
                    Transaction.builder()
                        .name("Equipment Purchase - Tractors")
                        .type("EXPENSE")
                        .amount(750000.0f)
                        .status("COMPLETED")
                        .isPositive(false)
                        .date(LocalDateTime.now().minusDays(15))
                        .build(),

                    Transaction.builder()
                        .name("Government Grant - Sustainable Farming")
                        .type("INCOME")
                        .amount(1200000.0f)
                        .status("COMPLETED")
                        .isPositive(true)
                        .date(LocalDateTime.now().minusDays(7))
                        .build(),

                    Transaction.builder()
                        .name("Seeds and Fertilizers Distribution")
                        .type("EXPENSE")
                        .amount(250000.0f)
                        .status("PENDING")
                        .isPositive(false)
                        .date(LocalDateTime.now().minusDays(3))
                        .build(),

                    Transaction.builder()
                        .name("Training Program Fees")
                        .type("INCOME")
                        .amount(45000.0f)
                        .status("COMPLETED")
                        .isPositive(true)
                        .date(LocalDateTime.now().minusDays(1))
                        .build(),

                    Transaction.builder()
                        .name("Insurance Premium Payment")
                        .type("EXPENSE")
                        .amount(180000.0f)
                        .status("SCHEDULED")
                        .isPositive(false)
                        .date(LocalDateTime.now().plusDays(5))
                        .build()
                );

                transactionRepository.saveAll(defaultTransactions);
                log.info("Successfully initialized {} sample transactions.", defaultTransactions.size());
            } else {
                log.info("Transactions already exist in the database. Skipping initialization.");
            }
        } catch (Exception e) {
            log.error("Error during TransactionInitializer execution: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to initialize transactions: " + e.getMessage(), e);
        }
    }

    private boolean transactionIsNull() {
        return transactionRepository.count() == 0;
    }
}
