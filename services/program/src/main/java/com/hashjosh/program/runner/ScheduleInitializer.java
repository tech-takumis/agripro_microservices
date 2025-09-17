package com.hashjosh.program.runner;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hashjosh.program.entity.Schedule;
import com.hashjosh.program.enums.SchedulePriority;
import com.hashjosh.program.enums.ScheduleType;
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
public class ScheduleInitializer {

    private final ScheduleRepository scheduleRepository;
    private final ObjectMapper objectMapper;

    @Bean
    CommandLineRunner seedSchedules() {
        return args -> {

            initializeSchedule();

        };
    }

    private boolean isScheduleNotNull() {
        return scheduleRepository.count() > 0;
    }

    private void initializeSchedule() {
        // --- Seed Schedules ---
        if (!isScheduleNotNull()) {
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
    }
}
