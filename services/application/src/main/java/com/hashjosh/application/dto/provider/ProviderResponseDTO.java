package com.hashjosh.application.dto.provider;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProviderResponseDTO {
    private UUID id;
    private String name;
    private String description;
    private LocalDateTime createdAt;
}
