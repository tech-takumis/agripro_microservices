package com.example.agriculture.runner;

import com.example.agriculture.entity.Program;
import com.example.agriculture.repository.ProgramRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Order(1)
@Slf4j
public class ProgramInitializer implements CommandLineRunner {

    private final ProgramRepository programRepository;

    @Override
    public void run(String... args) throws Exception {
        try {
            if (programIsNull()) {
                log.info("No programs found in the database. Initializing default programs...");

                List<Program> defaultPrograms = List.of(
                    Program.builder()
                        .name("Sustainable Rice Production Initiative")
                        .description("Program to enhance rice production through sustainable farming practices")
                        .budget(1000000.0f)
                        .completedPercentage(0)
                        .status("planned")
                        .startDate(LocalDateTime.now().plusMonths(1))
                        .endDate(LocalDateTime.now().plusMonths(13))
                        .beneficiaries(List.of())
                        .build(),

                    Program.builder()
                        .name("Farm Mechanization Support")
                        .description("Providing modern farming equipment and training to improve agricultural efficiency")
                        .budget(2500000.0f)
                        .completedPercentage(25)
                        .status("ongoing")
                        .startDate(LocalDateTime.now().minusMonths(2))
                        .endDate(LocalDateTime.now().plusMonths(10))
                        .beneficiaries(List.of())
                        .build(),

                    Program.builder()
                        .name("Organic Farming Transition")
                        .description("Supporting farmers in transitioning to organic farming methods")
                        .budget(800000.0f)
                        .completedPercentage(15)
                        .status("ongoing")
                        .startDate(LocalDateTime.now().minusMonths(1))
                        .endDate(LocalDateTime.now().plusMonths(11))
                        .beneficiaries(List.of())
                        .build(),

                    Program.builder()
                        .name("Agricultural Insurance Support")
                        .description("Providing insurance coverage for farmers against crop failures and natural disasters")
                        .budget(1500000.0f)
                        .completedPercentage(0)
                        .status("planned")
                        .startDate(LocalDateTime.now().plusMonths(2))
                        .endDate(LocalDateTime.now().plusMonths(14))
                        .beneficiaries(List.of())
                        .build(),

                    Program.builder()
                        .name("Smart Farming Technology Integration")
                        .description("Implementing IoT and smart technology solutions in farming practices")
                        .budget(3000000.0f)
                        .completedPercentage(10)
                        .status("ongoing")
                        .startDate(LocalDateTime.now().minusMonths(1))
                        .endDate(LocalDateTime.now().plusMonths(23))
                        .beneficiaries(List.of())
                        .build()
                );

                programRepository.saveAll(defaultPrograms);
                log.info("Successfully initialized {} default programs.", defaultPrograms.size());
            } else {
                log.info("Programs already exist in the database. Skipping initialization.");
            }
        } catch (Exception e) {
            log.error("Failed to initialize programs: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to initialize programs: " + e.getMessage(), e);
        }
    }

    private boolean programIsNull() {
        return programRepository.count() == 0;
    }
}
