package com.hashjosh.users.wrapper;

import com.hashjosh.users.dto.UserRegistrationRequest;
import com.hashjosh.users.entity.TenantType;

public record UserRegistrationRequestWrapper (
        TenantType tenantType,
        UserRegistrationRequest request
){
}
