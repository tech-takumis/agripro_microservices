package com.hashjosh.program.runner;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hashjosh.program.entity.Program;
import com.hashjosh.constant.program.enums.ProgramStatus;
import com.hashjosh.constant.program.enums.ProgramType;
import com.hashjosh.program.repository.ProgramRepository;
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

            // Rice Competitiveness Enhancement Fund (RCEF) Programs
            programs.add(createProgram(
                    "Rice Competitiveness Enhancement Fund (RCEF) - Seed Program",
                    ProgramType.TRAINING,
                    ProgramStatus.ACTIVE,
                    75,
                    objectMapper.createObjectNode()
                            .put("location", "National Rice Research Institute, Munoz, Nueva Ecija")
                            .put("trainer", "Dr. Emil Q. Javier")
                            .put("target_farmers", "5,000")
                            .put("budget", "PHP 1,500,000,000")
                            .put("duration", "2023-2028")
            ));

            programs.add(createProgram(
                    "RCEF - Mechanization Program",
                    ProgramType.WORKSHOP,
                    ProgramStatus.ACTIVE,
                    60,
                    objectMapper.createObjectNode()
                            .put("location", "Philippine Rice Research Institute")
                            .put("trainer", "Engr. Roberto G. Bayot")
                            .put("equipment_types", "Combine Harvesters, Transplanters")
                            .put("target_regions", "Regions I, II, III, IV-A, V")
                            .put("budget", "PHP 5,000,000,000")
            ));

            // National Rice Program
            programs.add(createProgram(
                    "National Rice Program - Hybrid Rice Production",
                    ProgramType.TRAINING,
                    ProgramStatus.COMPLETED,
                    100,
                    objectMapper.createObjectNode()
                            .put("location", "IRRI, Los Baños, Laguna")
                            .put("trainer", "Dr. Shaobing Peng")
                            .put("target_farmers", "10,000")
                            .put("yield_increase", "20%")
                            .put("implementation_period", "2020-2024")
            ));

            programs.add(createProgram(
                    "National Rice Program - Palay Check System",
                    ProgramType.WORKSHOP,
                    ProgramStatus.ACTIVE,
                    85,
                    objectMapper.createObjectNode()
                            .put("location", "Regional Training Centers")
                            .put("trainer", "DA-RFO Trainers")
                            .put("components", "P1-P6: Palay Check System")
                            .put("target_provinces", "Top 20 rice-producing provinces")
                            .put("farmer_organization", "Philippine Council of Agriculture")
            ));

            // Corn Program
            programs.add(createProgram(
                    "National Corn Program - Hybrid Corn Seed Distribution",
                    ProgramType.TRAINING,
                    ProgramStatus.ACTIVE,
                    70,
                    objectMapper.createObjectNode()
                            .put("location", "Bureau of Plant Industry, Los Baños")
                            .put("trainer", "Dr. Antonio G. Laforteza")
                            .put("seed_varieties", "IPB Varieties 9419, 9459")
                            .put("target_farmers", "8,000")
                            .put("budget", "PHP 800,000,000")
            ));

            programs.add(createProgram(
                    "Corn-Based Farmers Association Training",
                    ProgramType.WORKSHOP,
                    ProgramStatus.COMPLETED,
                    95,
                    objectMapper.createObjectNode()
                            .put("location", "Corn Program Implementation Units")
                            .put("trainer", "DA-Corn Program Specialists")
                            .put("topics", "Corn Production Technology, Post-Harvest")
                            .put("beneficiaries", "50,000 farmers")
                            .put("period", "2019-2023")
            ));

            // Organic Agriculture Program
            programs.add(createProgram(
                    "National Organic Agriculture Program (NOAP)",
                    ProgramType.TRAINING,
                    ProgramStatus.ACTIVE,
                    80,
                    objectMapper.createObjectNode()
                            .put("location", "Organic Agriculture Centers")
                            .put("trainer", "Organic Agriculture Specialists")
                            .put("certification", "NPOP, JAS, EU Organic")
                            .put("target_farmers", "15,000")
                            .put("budget", "PHP 1,200,000,000")
            ));

            programs.add(createProgram(
                    "Integrated Organic Farming System Training",
                    ProgramType.WORKSHOP,
                    ProgramStatus.ACTIVE,
                    65,
                    objectMapper.createObjectNode()
                            .put("location", "DA Regional Field Offices")
                            .put("trainer", "NOAP Technical Experts")
                            .put("modules", "Compost Production, Bio-Control Agents")
                            .put("target_provinces", "All 17 regions")
                            .put("duration", "3-day intensive training")
            ));

            // High-Value Crops Program
            programs.add(createProgram(
                    "High-Value Crops Development Program - Vegetable Production",
                    ProgramType.TRAINING,
                    ProgramStatus.COMPLETED,
                    100,
                    objectMapper.createObjectNode()
                            .put("location", "High Value Crops Development Center")
                            .put("trainer", "HVC Specialists")
                            .put("crops", "Tomato, Onion, Garlic, Pepper")
                            .put("market_linkage", "SM Supermarket, Puregold")
                            .put("beneficiaries", "12,000 farmers")
            ));

            programs.add(createProgram(
                    "Fruit Crops Production and Marketing Training",
                    ProgramType.WORKSHOP,
                    ProgramStatus.ACTIVE,
                    75,
                    objectMapper.createObjectNode()
                            .put("location", "Fruit Crops Centers")
                            .put("trainer", "DA-HVCDP Trainers")
                            .put("crops", "Mango, Banana, Pineapple, Durian")
                            .put("export_markets", "Middle East, China, Japan")
                            .put("budget", "PHP 2,500,000,000")
            ));

            programRepository.saveAll(programs);
            System.out.println("✅ Inserted " + programs.size() + " real DA programs.");
        } else {
            System.out.println("✅ Programs already exist. Skipping initialization.");
        }
    }

    private Program createProgram(String name, ProgramType type, ProgramStatus status, int completion, ObjectNode extraFields) {
        Program program = new Program();
        program.setName(name);
        program.setType(type);
        program.setStatus(status);
        program.setCompletion(completion);
        program.setCreatedAt(LocalDateTime.now());
        program.setUpdatedAt(LocalDateTime.now());
        program.setExtraFields(extraFields);
        return program;
    }
}
