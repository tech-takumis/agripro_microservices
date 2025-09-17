package com.hashjosh.program.runner;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hashjosh.program.dto.ProgramResponseDto;
import com.hashjosh.program.entity.Program;
import com.hashjosh.program.entity.Schedule;
import com.hashjosh.program.enums.ProgramStatus;
import com.hashjosh.program.enums.ProgramType;
import com.hashjosh.program.enums.SchedulePriority;
import com.hashjosh.program.enums.ScheduleType;
import com.hashjosh.program.repository.ProgramRepository;
import com.hashjosh.program.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class ProgramInitializer {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ProgramRepository programRepository;

    @Bean
    CommandLineRunner seedPrograms() {
        return args -> {

            initializePrograms();

        };
    }

    private boolean isProgramNotNull() {
        return programRepository.count() > 0;
    }

    private void initializePrograms() {
        if (!isProgramNotNull()) {
            List<Program> programs = new ArrayList<>();
            for (int i = 1; i <= 10; i++) {
                ObjectNode extra = objectMapper.createObjectNode();
                extra.put("location", "Barangay " + i);
                extra.put("trainer", "Trainer " + i);

                Program program = new Program();
                program.setName("Program " + i);
                program.setType(i % 2 == 0 ? ProgramType.TRAINING : ProgramType.WORKSHOP);
                program.setStatus(i % 3 == 0 ? ProgramStatus.COMPLETED : ProgramStatus.ACTIVE);
                program.setCompletion((i * 10) % 100);
                program.setExtraFields(extra);

                programs.add(program);
            }
            programRepository.saveAll(programs);
            System.out.println("✅ Inserted " + programs.size() + " programs.");
        }
    }
}
