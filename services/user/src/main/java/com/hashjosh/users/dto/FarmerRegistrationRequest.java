package com.hashjosh.users.dto;

import com.hashjosh.kafkacommon.user.TenantType;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@Data
public class FarmerRegistrationRequest {

    // Basic farmer information::
    private TenantType tenantId;
    private String rsbsaNumber;
    private String firstName;
    private String lastName;
    private String middleName;
    private String  email;
    private String  phoneNumber;

    // Address
    private String city;
    private String state;
    private String zipCode;
    private String country;

    // Farmer  farm location
    private String farmLocation;
    private String tenureStatus;
    private String farmSize;
    private String farmType;
    private String primaryCrop;
}
