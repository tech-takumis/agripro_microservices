package com.example.agriculture.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DesignatedResponse {
    private UUID userId;
    private String serviceType;
}
