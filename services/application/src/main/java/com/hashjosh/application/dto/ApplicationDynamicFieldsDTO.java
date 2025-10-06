package com.hashjosh.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ApplicationDynamicFieldsDTO {

    private Integer age;
    private String sex;
    private String tribe;
    private String address;
    @JsonProperty("crop_type")
    private String cropType;
    @JsonProperty("last_name")
    private String lastName;
    @JsonProperty("cover_type")
    private String coverType;
    @JsonProperty("first_name")
    private String firstName;
    @JsonProperty("lot_1_area")
    private Double lot1Area;
    @JsonProperty("middle_name")
    private String middleName;
    @JsonProperty("spouse_name")
    private String spouseName;
    @JsonProperty("civil_status")
    private String civilStatus;
    @JsonProperty("date_of_birth")
    private LocalDate dateOfBirth;

    @JsonProperty("lot_1_variety")
    private String lot1Variety;

    @JsonProperty("lot_1_location")
    private Lot1Location lot1Location;

    @JsonProperty("amount_of_cover")
    private Double amountOfCover;

    @JsonProperty("lot_1_soil_type")
    private String lot1SoilType;

    @JsonProperty("farmer_signature")
    private String farmerSignature;

    @JsonProperty("lot_1_boundaries")
    private Lot1Boundaries lot1Boundaries;

    @JsonProperty("lot_1_topography")
    private String lot1Topography;

    @JsonProperty("cell_phone_number")
    private String cellPhoneNumber;

    @JsonProperty("indigenous_people")
    private Boolean indigenousPeople;

    @JsonProperty("lot_1_date_sowing")
    private LocalDate lot1DateSowing;

    @JsonProperty("lot_1_date_harvest")
    private LocalDate lot1DateHarvest;

    @JsonProperty("lot_1_date_planting")
    private LocalDate lot1DatePlanting;

    @JsonProperty("lot_1_land_category")
    private String lot1LandCategory;

    @JsonProperty("primary_beneficiary")
    private String primaryBeneficiary;

    @JsonProperty("lot_1_planting_method")
    private String lot1PlantingMethod;

    @JsonProperty("lot_1_tenurial_status")
    private String lot1TenurialStatus;

    @JsonProperty("secondary_beneficiary")
    private String secondaryBeneficiary;

    @JsonProperty("lot_1_irrigation_source")
    private String lot1IrrigationSource;

    // inner DTOs
    @Data
    public static class Lot1Location {
        private String sitio;
        private String barangay;
        private String province;
        private String municipality;
    }

    @Data
    public static class Lot1Boundaries {
        private String east;
        private String west;
        private String north;
        private String south;
    }
}
