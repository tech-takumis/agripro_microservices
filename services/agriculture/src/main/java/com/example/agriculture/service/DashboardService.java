package com.example.agriculture.service;

import com.example.agriculture.dto.beneficiary.BeneficiaryResponse;
import com.example.agriculture.dto.dashboard.MunicipalDashboardResponse;
import com.example.agriculture.dto.program.ProgramResponse;
import com.example.agriculture.dto.transaction.TransactionResponse;
import com.example.agriculture.entity.Beneficiary;
import com.example.agriculture.entity.Program;
import com.example.agriculture.entity.Transaction;
import com.example.agriculture.repository.BeneficiaryRepository;
import com.example.agriculture.repository.ProgramRepository;
import com.example.agriculture.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

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
        long activePrograms = programRepository.count();
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
        List<BeneficiaryResponse> beneficiaries = getBeneficiariesForProgram(program.getId()).stream()
                .map(beneficiary -> BeneficiaryResponse.builder()
                        .beneficiaryId(beneficiary.getId())
                        .userId(beneficiary.getUserId())
                        .type(beneficiary.getType())
                        .build())
                .toList();

        return ProgramResponse.builder()
                .programId(program.getId())
                .programName(program.getName())
                .description(program.getDescription())
                .status(program.getStatus())
                .startDate(program.getStartDate())
                .endDate(program.getEndDate())
                .budget(program.getBudget())
                .completedPercentage(program.getCompletedPercentage())
                .beneficiaries(beneficiaries)
                .build();
    }

    private List<Beneficiary> getBeneficiariesForProgram(UUID programId) {
        return beneficiaryRepository.findByProgramId(programId, PageRequest.of(0, 100))
                .getContent();
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
