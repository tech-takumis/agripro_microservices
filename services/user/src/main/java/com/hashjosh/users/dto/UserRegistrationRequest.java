package com.hashjosh.users.dto;

import com.hashjosh.users.entity.UserType;
import lombok.Data;

@Data
public class UserRegistrationRequest {
    private String username;
    private String firstName;
    private String lastName;
    private String password;
    private String email;
    private String phoneNumber;
    private String address;
}

