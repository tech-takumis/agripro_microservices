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
    private final ScheduleRepository scheduleRepository;

    @Bean
    CommandLineRunner seedData() {
        return args -> {

            initializePrograms();
            // --- Seed Schedules ---
            if (scheduleRepository.count() == 0) {
                List<Schedule> schedules = new ArrayList<>();
                for (int i = 1; i <= 10; i++) {
                    ObjectNode meta = objectMapper.createObjectNode();
                    meta.put("farmerName", "Farmer " + i);
                    meta.put("location", "Field " + i);
                    meta.put("purpose", "Inspection " + i);

                    Schedule schedule = new Schedule();
                    schedule.setType(i % 2 == 0 ? ScheduleType.VISIT : ScheduleType.MEETING);
                    schedule.setScheduleDate(LocalDateTime.now().plusDays(i));
                    schedule.setPriority(i % 2 == 0 ? SchedulePriority.HIGH : SchedulePriority.MEDIUM);
                    schedule.setMetaData(meta);

                    schedules.add(schedule);
                }
                scheduleRepository.saveAll(schedules);
                System.out.println("✅ Inserted " + schedules.size() + " schedules.");
            }
        };
    }

    private void initializePrograms() {
        if (programRepository.count() == 0) {
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
