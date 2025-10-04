package com.hashjosh.pcic.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class PcicRequest {

    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String address;

    // profile
    private String mandate;
    private String vision;
    private String mission;
    private String coreValues;
    private String headOfficeAddress;
    private String pcicEmail;
    private String phone;
    private String website;
}
