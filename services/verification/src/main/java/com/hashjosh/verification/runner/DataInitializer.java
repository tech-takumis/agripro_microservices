package com.hashjosh.verification.runner;

import com.hashjosh.verification.entity.Batch;
import com.hashjosh.verification.repository.BatchRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final BatchRepository batchRepository;

    @Override
    public void run(String... args) throws Exception {
        if (batchRepository.count() > 0) {
            return;
        }

        UUID applicationTypeId = UUID.fromString("66bec1a2-c768-4382-9789-ef5de9ac4162");
        LocalDateTime now = LocalDateTime.now();

        List<Batch> batches = List.of(
            Batch.builder()
                .applicationTypeId(applicationTypeId)
                .name("Batch 1")
                .description("First batch for application type")
                .isAvailable(true)
                .startDate(now.minusDays(1))
                .endDate(now.plusDays(10))
                .maxApplications(10)
                .createdAt(now.minusDays(1))
                .build(),
            Batch.builder()
                .applicationTypeId(applicationTypeId)
                .name("Batch 2")
                .description("Second batch for application type")
                .isAvailable(true)
                .startDate(now.minusDays(2))
                .endDate(now.plusDays(5))
                .maxApplications(15)
                .createdAt(now.minusDays(2))
                .build(),
            Batch.builder()
                .applicationTypeId(applicationTypeId)
                .name("Batch 3")
                .description("Third batch for application type")
                .isAvailable(true)
                .startDate(now.minusDays(3))
                .endDate(now.plusDays(20))
                .maxApplications(20)
                .createdAt(now.minusDays(3))
                .build()
        );

        batchRepository.saveAll(batches);
    }
}
