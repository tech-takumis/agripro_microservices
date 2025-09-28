package com.hashjosh.users.dto;

import com.hashjosh.kafkacommon.user.TenantType;
import lombok.*;

import java.util.Set;
import java.util.UUID;

public class RegistrationRequest {

    @Getter
    @Setter
    @Data
    @Builder
    @AllArgsConstructor
    public static class FarmerRegistrationRequest {
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

    @Getter
    @Setter
    @Data
    @Builder
    @AllArgsConstructor
    public static class FarmerRegistrationRequestWrapper {
        private String username;
        private String password;
        private FarmerRegistrationRequest farmerRegistrationRequest;
    }


    @Getter
    @Setter
    @Data
    @Builder
    @AllArgsConstructor
    public static class FarmerCredendials {
        // Basic farmer information::
        private UUID userId;
        private String rsbsaNumber;
        private String username;
        private String password;
        private String firstName;
        private String lastName;
        private String middleName;
        private String  email;
        private String  phoneNumber;

    }


    @Getter
    @Setter
    @Data
    @Builder
    @AllArgsConstructor
    public static class StaffRegistrationRequest {
        private TenantType tenantId;
        private String username;
        private String firstName;
        private String lastName;
        private String password;
        private String email;
        private String phoneNumber;
        private String address;
        private Set<UUID> rolesId;
    }

    @Getter
    @Setter
    @Data
    @Builder
    @AllArgsConstructor
    public static class StaffCredentials {
        private TenantType tenantId;
        private UUID userId;
        private String username;
        private String password;
        private String firstName;
        private String lastName;
        private String email;
        private String phoneNumber;
        private String address;

    }
}
