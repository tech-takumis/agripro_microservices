package com.hashjosh.constant.user;

import lombok.*;

import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class UserResponseDTO {
    private UUID id;
    private String username;
    private String email;
    private String  firstName;
    private String lastName;
    private String tenant;
    private boolean active;
    private List<RoleResponse> roles;
}
