package com.hashjosh.userservicev2.seeder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hashjosh.userservicev2.dto.*;
import com.hashjosh.userservicev2.models.UserProfile;
import com.hashjosh.userservicev2.services.AuthorityService;
import com.hashjosh.userservicev2.services.RoleService;
import com.hashjosh.userservicev2.services.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Set;

@Configuration
public class StaffSeeder {

    @Bean
    CommandLineRunner seedStaff(
            AuthorityService authorityService,
            RoleService roleService,
            UserService userService,
            ObjectMapper objectMapper
    ) {
        return args -> {
            try {
                // 1️⃣ Create Authorities if not present
                List<AuthorityDto> authoritiesToCreate = List.of(
                        new AuthorityDto("CREATE_POLICY"),
                        new AuthorityDto("VIEW_POLICY"),
                        new AuthorityDto("APPROVE_POLICY")
                );

                List<AuthorityResponseDto> savedAuthorities = authorityService.saveAuthorities(authoritiesToCreate);
                System.out.println("✅ Authorities created: " + savedAuthorities.size());

                // Collect the authority IDs
                Set<Long> authorityIds = savedAuthorities.stream()
                        .map(AuthorityResponseDto::id)
                        .collect(java.util.stream.Collectors.toSet());

                // 2️⃣ Create Role and assign permissions
                RoleRequestDto roleRequest = new RoleRequestDto(
                        "STAFF",
                        authorityIds
                );

                RoleResponseDto savedRole = roleService.createRole(roleRequest);
                System.out.println("✅ Role created: " + savedRole.name());

                // 3️⃣ Register Staff linked to the Role
                JsonNode roleDetails = objectMapper.readTree("""
                    { "dateOfBirt": "feb 04 2004" }
                """);

                UserRequestDto staffDto = getUserRequestDto(roleDetails, savedRole);

                userService.register(staffDto);
                System.out.println("✅ Staff registered successfully");

            } catch (Exception e) {
                System.err.println("⚠️ Failed to seed staff: " + e.getMessage());
            }
        };
    }

    private static UserRequestDto getUserRequestDto(JsonNode roleDetails, RoleResponseDto savedRole) {
        UserProfileRequest profileRequest = new UserProfileRequest(
                UserProfile.ProfileType.STAFF,
                roleDetails
        );

        UserRequestDto staffDto = new UserRequestDto(
                "Markhen Deo",
                "pracullos",
                "pracullosdeo28@gmail.com",
                "Male",
                "09501986109",
                "Single",
                "P-7 Charito Bayugam City",
                savedRole.id(), // 🔑 assign role ID from DB
                profileRequest
        );
        return staffDto;
    }
}
