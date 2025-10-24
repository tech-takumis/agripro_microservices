package com.hashjosh.application.runner;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.hashjosh.application.enums.FieldType;
import com.hashjosh.application.model.*;
import com.hashjosh.application.repository.ApplicationProviderRepository;
import com.hashjosh.application.repository.ApplicationTypeRepository;
import com.hashjosh.application.repository.BatchRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApplicationTypeInitializer implements CommandLineRunner {

    private final EntityManager entityManager;
    private final ApplicationTypeRepository applicationTypeRepository;
    private final ObjectMapper objectMapper;
    private final ApplicationProviderRepository applicationProviderRepository;
    private final BatchRepository batchRepository;


    public boolean isApplicationNotNull(){
        return applicationTypeRepository.count() > 0;
    }
    @Override
    @Transactional
    public void run(String... args) {
        if(isApplicationNotNull()){
            log.info("Application Type already exists initialization skipped!");
            return;
        }

        ApplicationProvider provider1 = ApplicationProvider.builder()
                .name("Agriculture")
                .description("Agriculture related application forms")
                .createdAt(LocalDateTime.now())
                .build();

        applicationProviderRepository.save(provider1);
        ApplicationProvider provider2 = ApplicationProvider.builder()
                .name("PCIC")
                .description("Pcic related application forms")
                .createdAt(LocalDateTime.now())
                .build();

        applicationProviderRepository.save(provider2);


        // Application Type 1: Crop Insurance Application
        ApplicationType cropInsurance = ApplicationType.builder()
                .name("Crop Insurance Application")
                .description("Application form for insuring rice or corn crops")
                .layout("form")
                .provider(provider1)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .sections(new ArrayList<>())
                .build();

        // Sections for Crop Insurance
        List<ApplicationSection> cropInsuranceSections = new ArrayList<>();

        // Section I: Basic Information
        ApplicationSection basicInfoSection = ApplicationSection.builder()
                .title("Basic Information")
                .applicationType(cropInsurance)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .fields(new ArrayList<>())
                .build();
        cropInsuranceSections.add(basicInfoSection);

        // Fields for Basic Information
        List<ApplicationField> basicInfoFields = new ArrayList<>();
        basicInfoFields.add(createField("last_name", "Last Name", FieldType.TEXT, true, null, basicInfoSection));
        basicInfoFields.add(createField("first_name", "First Name", FieldType.TEXT, true, null, basicInfoSection));
        basicInfoFields.add(createField("middle_name", "Middle Name", FieldType.TEXT, false, null, basicInfoSection));
        basicInfoFields.add(createField("address", "Address", FieldType.TEXT, true, null, basicInfoSection));
        basicInfoFields.add(createField("cell_phone_number", "Cell Phone Number", FieldType.TEXT, false, null, basicInfoSection));
        basicInfoFields.add(createField("sex", "Sex", FieldType.SELECT, true, createChoices(new String[]{"Male", "Female"}), basicInfoSection));
        basicInfoFields.add(createField("age", "Age", FieldType.NUMBER, false, null, basicInfoSection));
        basicInfoFields.add(createField("date_of_birth", "Date of Birth", FieldType.DATE, false, null, basicInfoSection));
        basicInfoFields.add(createField("indigenous_people", "Indigenous People", FieldType.BOOLEAN, false, null, basicInfoSection));
        basicInfoFields.add(createField("tribe", "Tribe", FieldType.TEXT, false, null, basicInfoSection));
        basicInfoFields.add(createField("civil_status", "Civil Status", FieldType.SELECT, true, createChoices(new String[]{"Single", "Married", "Widow/Widower", "Separated"}), basicInfoSection));
        basicInfoFields.add(createField("spouse_name", "Name of Spouse", FieldType.TEXT, false, null, basicInfoSection));
        basicInfoFields.add(createField("primary_beneficiary", "Primary Beneficiary", FieldType.TEXT, false, null, basicInfoSection));
        basicInfoFields.add(createField("secondary_beneficiary", "Secondary Beneficiary", FieldType.TEXT, false, null, basicInfoSection));
        basicInfoSection.setFields(basicInfoFields);

        // Section B: The Farm
        ApplicationSection farmSection = ApplicationSection.builder()
                .title("The Farm")
                .applicationType(cropInsurance)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .fields(new ArrayList<>())
                .build();
        cropInsuranceSections.add(farmSection);

        // Fields for The Farm (Lot 1, extensible for Lot 2, Lot 3)
        List<ApplicationField> farmFields = new ArrayList<>();
        farmFields.add(createField("lot_1_area", "Lot 1 Area (ha)", FieldType.NUMBER, true, null, farmSection));
        farmFields.add(createField("lot_1_location", "Lot 1 Location", FieldType.JSON, true, null, farmSection)); // Sitio, Barangay, Municipality, Province
        farmFields.add(createField("lot_1_boundaries", "Lot 1 Boundaries", FieldType.JSON, false, null, farmSection)); // North, South, East, West
        farmFields.add(createField("lot_1_variety", "Lot 1 Variety", FieldType.TEXT, true, null, farmSection));
        farmFields.add(createField("lot_1_planting_method", "Lot 1 Planting Method", FieldType.SELECT, true, createChoices(new String[]{"Direct Seeding", "Transplanting"}), farmSection));
        farmFields.add(createField("lot_1_date_sowing", "Lot 1 Date of Sowing", FieldType.DATE, false, null, farmSection));
        farmFields.add(createField("lot_1_date_planting", "Lot 1 Date of Planting", FieldType.DATE, true, null, farmSection));
        farmFields.add(createField("lot_1_date_harvest", "Lot 1 Date of Harvest", FieldType.DATE, true, null, farmSection));
        farmFields.add(createField("lot_1_land_category", "Lot 1 Land Category", FieldType.SELECT, true, createChoices(new String[]{"Irrigated", "Rainfed", "Upland"}), farmSection));
        farmFields.add(createField("lot_1_soil_type", "Lot 1 Soil Type", FieldType.SELECT, true, createChoices(new String[]{"Clay Loam", "Silty Clay Loam", "Silty Loam", "Sandy Loam", "Others"}), farmSection));
        farmFields.add(createField("lot_1_topography", "Lot 1 Topography", FieldType.SELECT, true, createChoices(new String[]{"Flat", "Rolling", "Hilly"}), farmSection));
        farmFields.add(createField("lot_1_irrigation_source", "Lot 1 Source of Irrigation", FieldType.SELECT, false, createChoices(new String[]{"NIA/CIS", "Deep Well", "SWIP", "Shallow Tube Well"}), farmSection));
        farmFields.add(createField("lot_1_tenurial_status", "Lot 1 Tenurial Status", FieldType.SELECT, true, createChoices(new String[]{"Owner", "Lessee"}), farmSection));
        // Add similar fields for Lot 2, Lot 3 if needed
        farmSection.setFields(farmFields);

        // Section C: The Coverage
        ApplicationSection coverageSection = ApplicationSection.builder()
                .title("The Coverage")
                .applicationType(cropInsurance)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .fields(new ArrayList<>())
                .build();
        cropInsuranceSections.add(coverageSection);

        // Fields for The Coverage
        List<ApplicationField> coverageFields = new ArrayList<>();
        coverageFields.add(createField("crop_type", "Crop", FieldType.SELECT, true, createChoices(new String[]{"Rice", "Corn"}), coverageSection));
        coverageFields.add(createField("cover_type", "Type of Cover", FieldType.SELECT, true, createChoices(new String[]{"Multi-Risk", "Natural Disaster"}), coverageSection));
        coverageFields.add(createField("amount_of_cover", "Amount of Cover", FieldType.NUMBER, true, null, coverageSection));
        coverageSection.setFields(coverageFields);

        // Section II: Certification
        ApplicationSection certificationSection = ApplicationSection.builder()
                .title("Certification")
                .applicationType(cropInsurance)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .fields(new ArrayList<>())
                .build();
        cropInsuranceSections.add(certificationSection);

        // Fields for Certification
        List<ApplicationField> certificationFields = new ArrayList<>();
        certificationFields.add(createField("farmer_signature", "Farmer Signature/Thumb Mark", FieldType.SIGNATURE, true, null, certificationSection));
        certificationSection.setFields(certificationFields);

        cropInsurance.setSections(cropInsuranceSections);

        Batch batch = Batch.builder()
                .name("BATCH-001")
                .description("Batch 001 for crop insurance applications")
                .isAvailable(true)
                .startDate(LocalDateTime.now())
                .applicationType(cropInsurance)
                .endDate(LocalDateTime.now().plusMonths(1))
                .createdAt(LocalDateTime.now())
                .build();
        batchRepository.save(batch);
        entityManager.persist(cropInsurance);

        // Application Type 2: Claim for Indemnity
        ApplicationType claimIndemnity = ApplicationType.builder()
                .name("Claim for Indemnity")
                .description("Claim form for indemnity of insured high-value crops")
                .layout("form")
                .provider(provider2)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .sections(new ArrayList<>())
                .build();

        // Sections for Claim for Indemnity
        List<ApplicationSection> claimIndemnitySections = new ArrayList<>();

        // Section I: Basic Information
        ApplicationSection claimBasicInfoSection = ApplicationSection.builder()
                .title("Basic Information")
                .applicationType(claimIndemnity)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .fields(new ArrayList<>())
                .build();
        claimIndemnitySections.add(claimBasicInfoSection);

        // Fields for Basic Information
        List<ApplicationField> claimBasicInfoFields = new ArrayList<>();
        claimBasicInfoFields.add(createField("farmer_name", "Name of Farmer-Assured", FieldType.TEXT, true, null, claimBasicInfoSection));
        claimBasicInfoFields.add(createField("address", "Address", FieldType.TEXT, true, null, claimBasicInfoSection));
        claimBasicInfoFields.add(createField("cell_phone_number", "Cell Phone Number", FieldType.TEXT, false, null, claimBasicInfoSection));
        claimBasicInfoFields.add(createField("farm_location", "Location of Farm", FieldType.TEXT, true, null, claimBasicInfoSection));
        claimBasicInfoFields.add(createField("insured_crops", "Insured Crops", FieldType.TEXT, true, null, claimBasicInfoSection));
        claimBasicInfoFields.add(createField("area_insured", "Area Insured (in hectares)", FieldType.NUMBER, true, null, claimBasicInfoSection));
        claimBasicInfoFields.add(createField("variety_planted", "Variety Planted", FieldType.TEXT, true, null, claimBasicInfoSection));
        claimBasicInfoFields.add(createField("date_planting", "Actual Date of Planting", FieldType.DATE, true, null, claimBasicInfoSection));
        claimBasicInfoFields.add(createField("cic_no", "CIC Number", FieldType.TEXT, true, null, claimBasicInfoSection));
        claimBasicInfoFields.add(createField("underwriter_cooperative", "Underwriter/Cooperative", FieldType.TEXT, false, null, claimBasicInfoSection));
        claimBasicInfoSection.setFields(claimBasicInfoFields);

        // Section II: Damage Indicators
        ApplicationSection damageIndicatorsSection = ApplicationSection.builder()
                .title("Damage Indicators")
                .applicationType(claimIndemnity)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .fields(new ArrayList<>())
                .build();
        claimIndemnitySections.add(damageIndicatorsSection);

        // Fields for Damage Indicators
        List<ApplicationField> damageIndicatorsFields = new ArrayList<>();
        damageIndicatorsFields.add(createField("cause_of_loss", "Cause of Loss", FieldType.FILE, true, null, damageIndicatorsSection));
        damageIndicatorsFields.add(createField("date_of_loss", "Date of Loss Occurrence", FieldType.DATE, true, null, damageIndicatorsSection));
        damageIndicatorsFields.add(createField("cultivation_stage", "Age/Stage of Cultivation at Time of Loss", FieldType.TEXT, true, null, damageIndicatorsSection));
        damageIndicatorsFields.add(createField("area_damaged", "Area Damaged", FieldType.NUMBER, true, null, damageIndicatorsSection));
        damageIndicatorsFields.add(createField("extent_of_damage", "Extent/Degree of Damage", FieldType.TEXT, true, null, damageIndicatorsSection));
        damageIndicatorsFields.add(createField("expected_harvest_date", "Expected Date of Harvest", FieldType.DATE, true, null, damageIndicatorsSection));
        damageIndicatorsFields.add(createField("production_cost", "Cost of Production Inputs at Time of Loss", FieldType.NUMBER, true, null, damageIndicatorsSection));
        damageIndicatorsSection.setFields(damageIndicatorsFields);

        // Section III: Location Sketch Plan
        ApplicationSection locationSketchSection = ApplicationSection.builder()
                .title("Location Sketch Plan of Damaged Insured Crops")
                .applicationType(claimIndemnity)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .fields(new ArrayList<>())
                .build();
        claimIndemnitySections.add(locationSketchSection);

        // Fields for Location Sketch Plan
        List<ApplicationField> locationSketchFields = new ArrayList<>();
        locationSketchFields.add(createField("lot_1_area", "Lot 1 Area (ha)", FieldType.NUMBER, false, null, locationSketchSection));
        locationSketchFields.add(createField("lot_1_boundaries", "Lot 1 Boundaries", FieldType.JSON, false, null, locationSketchSection)); // North, South, East, West

        locationSketchFields.add(createField("lot_2_area", "Lot 2 Area (ha)", FieldType.NUMBER, false, null, locationSketchSection));
        locationSketchFields.add(createField("lot_2_boundaries", "Lot 2 Boundaries", FieldType.JSON, false, null, locationSketchSection)); // North, South, East, West

        locationSketchFields.add(createField("lot_3_area", "Lot 3 Area (ha)", FieldType.NUMBER, false, null, locationSketchSection));
        locationSketchFields.add(createField("lot_3_boundaries", "Lot 3 Boundaries", FieldType.JSON, false, null, locationSketchSection)); // North, South, East, West

        locationSketchFields.add(createField("lot_4_area", "Lot 4 Area (ha)", FieldType.NUMBER, false, null, locationSketchSection));
        locationSketchFields.add(createField("lot_4_boundaries", "Lot 4 Boundaries", FieldType.JSON, false, null, locationSketchSection)); // North, South, East, West
        // Add similar fields for Lot 2, Lot 3, Lot 4 if needed
        locationSketchSection.setFields(locationSketchFields);

        Batch batch2 = Batch.builder()
                .name("BATCH-002")
                .description("Batch 002 for claim for indemnity applications")
                .isAvailable(true)
                .startDate(LocalDateTime.now())
                .applicationType(claimIndemnity)
                .endDate(LocalDateTime.now().plusMonths(1))
                .createdAt(LocalDateTime.now())
                .build();

        batchRepository.save(batch2);

        claimIndemnity.setSections(claimIndemnitySections);
        entityManager.persist(claimIndemnity);
    }

    private ApplicationField createField(String key, String fieldName, FieldType fieldType, boolean required, JsonNode choices, ApplicationSection section) {
        return ApplicationField.builder()
                .key(key)
                .fieldName(fieldName)
                .fieldType(fieldType)
                .required(required)
                .choices(choices)
                .applicationSection(section)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private JsonNode createChoices(String[] options) {
        ArrayNode choices = objectMapper.createArrayNode();
        for (String option : options) {
            choices.add(option);
        }
        return choices;
    }
}
