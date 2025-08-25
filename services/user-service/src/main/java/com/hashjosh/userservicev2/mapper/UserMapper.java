package com.hashjosh.userservicev2.mapper;

import com.hashjosh.userservicev2.dto.AuthenticatedUserDto;
import com.hashjosh.userservicev2.dto.UserProfileRequest;
import com.hashjosh.userservicev2.dto.UserRequestDto;
import com.hashjosh.userservicev2.dto.UserResponseDto;
import com.hashjosh.userservicev2.models.Role;
import com.hashjosh.userservicev2.models.Rsbsa;
import com.hashjosh.userservicev2.models.User;
import com.hashjosh.userservicev2.models.UserProfile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

@Service
public class UserMapper {

    private final PasswordEncoder passwordEncoder;

    public UserMapper(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public User toRegisterStaff(UserRequestDto dto, Role role) {
        return User.builder()
                .firstName(dto.firstName())
                .lastName(dto.lastName())
                .username(dto.email().split("@")[0])
                .password(passwordEncoder.encode("123456789"))
                .gender(dto.gender())
                .civilStatus(dto.civilStatus())
                .role(role)
                .contactNumber(dto.contactNumber())
                .address(dto.address())
                .email(dto.email())
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .build();
    }


    public User rsbsaToFarmer(Rsbsa rsbsa, Role role) {
        return User.builder()
                .email(rsbsa.getEmail())
                .username(rsbsa.getRsbsaId())
                .firstName(rsbsa.getFirstName())
                .lastName(rsbsa.getLastName())
                .deleted(false)
                .role(role)
                .gender(rsbsa.getGender())
                .civilStatus(rsbsa.getCivilStatus())
                .contactNumber(rsbsa.getContactNumber())
                .address(rsbsa.getAddress())
                .email(rsbsa.getEmail())
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .build();
    }

    public UserProfile toUserProfile(UserProfileRequest dto, User user) {
        return UserProfile.builder()
                .profileType(UserProfile.ProfileType.valueOf(dto.profileType().name().toUpperCase()))
                .user(user)
                .roleDetails(dto.roleDetails())
                .build();
    }

    public AuthenticatedUserDto toAuthenticatedUser(User user) {
        return new AuthenticatedUserDto(
            user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getRole().getName()
        );
    }

    public UserResponseDto toStaffResponse(User user) {
        return new UserResponseDto(
            user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getRole().getName(),
                user.getGender(),
                user.getContactNumber(),
                user.getCivilStatus(),
                user.getAddress(),
                user.getUserProfile().getProfileType().name(),
                user.getUserProfile().getRoleDetails()
        );
    }
}
