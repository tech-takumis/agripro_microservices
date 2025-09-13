package com.hashjosh.rsbsa.dto;

public record RsbsaRequestDto(
        String rsbsaId,
        String firstName,
        String lastName,
        String middleName,
        String gender,
        String civilStatus,
        String address,
        String barangay,
        String dateOfBirth,
        String municipality,
        String province,
        String contactNumber,
        String email,
        String farmingType,
        String primaryCrop,
        String secondaryCrop,
        Float farmArea,
        String farmLocation,
        String tenureStatus,
        String sourceOfIncome,
        float estimatedIncome,
        Integer householdSize,
        String educationLevel,
        boolean withDisability

) {
}
