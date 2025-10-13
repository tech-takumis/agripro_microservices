package com.example.agriculture.service;

import com.example.agriculture.dto.dashboard.MunicipalDashboardResponse;
import com.example.agriculture.dto.program.ProgramResponse;
import com.example.agriculture.dto.transaction.TransactionResponse;
import com.example.agriculture.entity.Program;
import com.example.agriculture.entity.Transaction;
import com.example.agriculture.repository.BeneficiaryRepository;
import com.example.agriculture.repository.ProgramRepository;
import com.example.agriculture.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardService {
    private final ProgramRepository programRepository;
    private final TransactionRepository transactionRepository;
    private final BeneficiaryRepository beneficiaryRepository;
    public MunicipalDashboardResponse getMunicipalDashboardData() {
        // First we need to fetch the data from various sources
        // First from Programs
        int activePrograms = programRepository.countByStatus("ACTIVE");
        List<ProgramResponse> programs = programRepository.findAll().stream()
                .map(this::toProgramResponse)
                .toList();
        // Then from Transactions

        List<TransactionResponse> transactions = transactionRepository.findAll().stream()
                .map(this::toTransactionResponse)
                .toList();

        return MunicipalDashboardResponse.builder()
                .dashboardId(UUID.randomUUID())
                .activePrograms(activePrograms)
                .programs(programs)
                .transactions(transactions)
                .build();
    }

    private ProgramResponse toProgramResponse(Program program) {
        return ProgramResponse.builder()
                .programId(program.getId())
                .programName(program.getName())
                .description(program.getDescription())
                .status(program.getStatus())
                .startDate(program.getStartDate())
                .endDate(program.getEndDate())
                .build();
    }

    private TransactionResponse toTransactionResponse(Transaction transaction) {
        return TransactionResponse.builder()
                .transactionId(transaction.getId())
                .amount(transaction.getAmount())
                .date(transaction.getDate())
                .type(transaction.getType())
                .build();
    }
}
