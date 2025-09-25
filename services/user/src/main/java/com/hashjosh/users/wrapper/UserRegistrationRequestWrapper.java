package com.hashjosh.users.wrapper;

import com.hashjosh.users.dto.UserRegistrationRequest;
import com.hashjosh.users.dto.FarmerRegistrationRequest;
import com.hashjosh.kafkacommon.user.TenantType;
import lombok.Getter;

@Getter
public class UserRegistrationRequestWrapper {
    private final TenantType tenantType;
    private final UserRegistrationRequest userRequest;
    private final FarmerRegistrationRequest farmerRequest;

    public UserRegistrationRequestWrapper(TenantType tenantType, UserRegistrationRequest userRequest) {
        this.tenantType = tenantType;
        this.userRequest = userRequest;
        this.farmerRequest = null;
    }

    public UserRegistrationRequestWrapper(TenantType tenantType, UserRegistrationRequest userRequest, FarmerRegistrationRequest farmerRequest) {
        this.tenantType = tenantType;
        this.userRequest = userRequest;
        this.farmerRequest = farmerRequest;
    }
}