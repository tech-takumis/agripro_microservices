package com.hashjosh.transaction.runner;

import com.hashjosh.transaction.entity.Transaction;
import com.hashjosh.transaction.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataIntializer implements CommandLineRunner {

    private final TransactionRepository transactionRepository;

    @Override
    public void run(String... args) throws Exception {
        if (transactionRepository.count() == 0) {
            List<Transaction> transactions = Arrays.asList(
                // Income transactions
                Transaction.builder()
                    .name("Salary Deposit")
                    .type("INCOME")
                    .amount(5000.00f)
                    .status("COMPLETED")
                    .isPositive(true)
                    .date(LocalDateTime.now().minusDays(1))
                    .build(),
                    
                Transaction.builder()
                    .name("Freelance Payment")
                    .type("INCOME")
                    .amount(1200.00f)
                    .status("COMPLETED")
                    .isPositive(true)
                    .date(LocalDateTime.now().minusDays(2))
                    .build(),
                    
                // Expense transactions
                Transaction.builder()
                    .name("Rent Payment")
                    .type("EXPENSE")
                    .amount(1500.00f)
                    .status("COMPLETED")
                    .isPositive(false)
                    .date(LocalDateTime.now().minusDays(3))
                    .build(),
                    
                Transaction.builder()
                    .name("Grocery Shopping")
                    .type("EXPENSE")
                    .amount(200.00f)
                    .status("COMPLETED")
                    .isPositive(false)
                    .date(LocalDateTime.now().minusDays(4))
                    .build(),
                    
                Transaction.builder()
                    .name("Utility Bills")
                    .type("EXPENSE")
                    .amount(150.00f)
                    .status("PENDING")
                    .isPositive(false)
                    .date(LocalDateTime.now())
                    .build(),
                    
                // Investment transactions
                Transaction.builder()
                    .name("Stock Investment")
                    .type("INVESTMENT")
                    .amount(2000.00f)
                    .status("COMPLETED")
                    .isPositive(false)
                    .date(LocalDateTime.now().minusDays(5))
                    .build(),
                    
                Transaction.builder()
                    .name("Dividend Payment")
                    .type("INCOME")
                    .amount(100.00f)
                    .status("COMPLETED")
                    .isPositive(true)
                    .date(LocalDateTime.now().minusDays(6))
                    .build(),
                    
                // Transfer transactions
                Transaction.builder()
                    .name("Bank Transfer to Savings")
                    .type("TRANSFER")
                    .amount(1000.00f)
                    .status("COMPLETED")
                    .isPositive(false)
                    .date(LocalDateTime.now().minusDays(7))
                    .build(),
                    
                Transaction.builder()
                    .name("Mobile Phone Bill")
                    .type("EXPENSE")
                    .amount(50.00f)
                    .status("PENDING")
                    .isPositive(false)
                    .date(LocalDateTime.now().plusDays(1))
                    .build(),
                    
                Transaction.builder()
                    .name("Insurance Premium")
                    .type("EXPENSE")
                    .amount(300.00f)
                    .status("SCHEDULED")
                    .isPositive(false)
                    .date(LocalDateTime.now().plusDays(2))
                    .build()
            );

            transactionRepository.saveAll(transactions);
            log.info("Sample transactions initialized successfully");
        }
    }
}