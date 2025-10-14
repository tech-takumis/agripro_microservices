package com.example.agriculture.dto.dashboard;

import com.example.agriculture.dto.program.ProgramResponse;
import com.example.agriculture.dto.transaction.TransactionResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MunicipalDashboardResponse {
    private UUID dashboardId;
    private long activePrograms;
    private List<ProgramResponse> programs;
    private List<TransactionResponse> transactions;
}
