package com.hashjosh.users.wrapper;

import com.hashjosh.users.dto.UserRegistrationRequest;
import com.hashjosh.users.entity.UserType;

public record UserRegistrationRequestWrapper (
        UserType userType,
        UserRegistrationRequest request
){
}
