package com.hashjosh.program.runner;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.hashjosh.program.entity.Schedule;
import com.hashjosh.constant.program.enums.SchedulePriority;
import com.hashjosh.constant.program.enums.ScheduleType;
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
        if (!isScheduleNotNull()) {
            List<Schedule> schedules = new ArrayList<>();

            // RCEF Seed Program Schedules
            schedules.add(createSchedule(
                    ScheduleType.VISIT,
                    LocalDateTime.of(2025, 9, 20, 9, 0),
                    SchedulePriority.HIGH,
                    objectMapper.createObjectNode()
                            .put("farmer_name", "Juan Dela Cruz")
                            .put("location", "Barangay San Isidro, Bayugan City")
                            .put("purpose", "Hybrid Rice Seed Distribution")
                            .put("program", "RCEF Seed Program")
                            .put("attendees", 50)
                            .put("contact_person", "Barangay Captain Jose Santos")
            ));

            schedules.add(createSchedule(
                    ScheduleType.MEETING,
                    LocalDateTime.of(2025, 9, 22, 14, 0),
                    SchedulePriority.MEDIUM,
                    objectMapper.createObjectNode()
                            .put("farmer_name", "Maria Santos")
                            .put("location", "DA-RFO Caraga Office")
                            .put("purpose", "RCEF Mechanization Program Briefing")
                            .put("program", "RCEF Mechanization")
                            .put("agenda", "Equipment Procurement, Training Schedule")
                            .put("participants", "Farmers Cooperative Officers")
            ));

            // National Rice Program Schedules
            schedules.add(createSchedule(
                    ScheduleType.TRAINING,
                    LocalDateTime.of(2025, 9, 25, 8, 30),
                    SchedulePriority.HIGH,
                    objectMapper.createObjectNode()
                            .put("farmer_name", "Pedro Ramirez")
                            .put("location", "PhilRice Training Center, Nueva Ecija")
                            .put("purpose", "Palay Check System Training")
                            .put("program", "National Rice Program")
                            .put("duration", "3 days")
                            .put("topics", "P1-P6 Implementation, Field Management")
            ));

            schedules.add(createSchedule(
                    ScheduleType.WORKSHOP,
                    LocalDateTime.of(2025, 9, 28, 10, 0),
                    SchedulePriority.MEDIUM,
                    objectMapper.createObjectNode()
                            .put("farmer_name", "Ana Garcia")
                            .put("location", "IRRI Los Baños")
                            .put("purpose", "Hybrid Rice Technology Workshop")
                            .put("program", "National Rice Program")
                            .put("facilitator", "Dr. Shaobing Peng")
                            .put("target_group", "Progressive Farmers")
            ));

            // Corn Program Schedules
            schedules.add(createSchedule(
                    ScheduleType.VISIT,
                    LocalDateTime.of(2025, 10, 2, 13, 0),
                    SchedulePriority.HIGH,
                    objectMapper.createObjectNode()
                            .put("farmer_name", "Jose Fernandez")
                            .put("location", "Corn Demo Farm, Isabela")
                            .put("purpose", "Hybrid Corn Seed Assessment")
                            .put("program", "National Corn Program")
                            .put("crop_variety", "IPB Var. 9419")
                            .put("field_size", "5 hectares")
            ));

            schedules.add(createSchedule(
                    ScheduleType.MEETING,
                    LocalDateTime.of(2025, 10, 5, 15, 0),
                    SchedulePriority.LOW,
                    objectMapper.createObjectNode()
                            .put("farmer_name", "Luz Morales")
                            .put("location", "DA-Corn Program Office")
                            .put("purpose", "Corn Farmers Association Meeting")
                            .put("program", "National Corn Program")
                            .put("agenda", "Input Procurement, Market Linkages")
                            .put("expected_attendees", 30)
            ));

            // Organic Agriculture Program Schedules
            schedules.add(createSchedule(
                    ScheduleType.TRAINING,
                    LocalDateTime.of(2025, 10, 8, 9, 0),
                    SchedulePriority.HIGH,
                    objectMapper.createObjectNode()
                            .put("farmer_name", "Roberto Lim")
                            .put("location", "Organic Agriculture Center, Laguna")
                            .put("purpose", "Organic Farming Certification Training")
                            .put("program", "NOAP")
                            .put("certification", "NPOP Standards")
                            .put("duration", "5 days")
            ));

            schedules.add(createSchedule(
                    ScheduleType.WORKSHOP,
                    LocalDateTime.of(2025, 10, 12, 14, 0),
                    SchedulePriority.MEDIUM,
                    objectMapper.createObjectNode()
                            .put("farmer_name", "Carmen Reyes")
                            .put("location", "DA-RFO Region 4-A")
                            .put("purpose", "Compost Production Workshop")
                            .put("program", "NOAP")
                            .put("materials", "Vermicompost, Biochar")
                            .put("target_farmers", 25)
            ));

            // High-Value Crops Program Schedules
            schedules.add(createSchedule(
                    ScheduleType.VISIT,
                    LocalDateTime.of(2025, 10, 15, 11, 0),
                    SchedulePriority.HIGH,
                    objectMapper.createObjectNode()
                            .put("farmer_name", "Miguel Tan")
                            .put("location", "Mango Demo Farm, Guimaras")
                            .put("purpose", "High-Value Crops Assessment")
                            .put("program", "HVCDP")
                            .put("crop_type", "Mango")
                            .put("variety", "Carabao Mango")
            ));

            schedules.add(createSchedule(
                    ScheduleType.MEETING,
                    LocalDateTime.of(2025, 10, 18, 16, 0),
                    SchedulePriority.MEDIUM,
                    objectMapper.createObjectNode()
                            .put("farmer_name", "Sofia Cruz")
                            .put("location", "HVCDP Central Office")
                            .put("purpose", "Vegetable Production Planning")
                            .put("program", "HVCDP")
                            .put("crops", "Tomato, Onion, Garlic")
                            .put("market_target", "Metro Manila Markets")
            ));

            scheduleRepository.saveAll(schedules);
            System.out.println("✅ Inserted " + schedules.size() + " real DA program schedules.");
        } else {
            System.out.println("✅ Schedules already exist. Skipping initialization.");
        }
    }

    private Schedule createSchedule(ScheduleType type, LocalDateTime scheduleDate, SchedulePriority priority, ObjectNode metaData) {
        Schedule schedule = new Schedule();
        schedule.setType(type);
        schedule.setScheduleDate(scheduleDate);
        schedule.setPriority(priority);
        schedule.setCreatedAt(LocalDateTime.now());
        schedule.setUpdatedAt(LocalDateTime.now());
        schedule.setMetaData(metaData);
        return schedule;
    }
}