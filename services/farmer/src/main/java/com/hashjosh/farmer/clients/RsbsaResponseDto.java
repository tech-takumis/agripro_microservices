package com.hashjosh.farmer.clients;

public record RsbsaResponseDto(
        String rsbsaId,
        String firstName,
        String lastName,
        String middleName,
        String gender,
        String civilStatus,
        String address,
        String barangay,
        String municipality,
        String province,
        String contactNumber,
        String email,
        String farmingType,
        String primaryCrop,
        String secondaryCrop,
        String dateOfBirth,
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
