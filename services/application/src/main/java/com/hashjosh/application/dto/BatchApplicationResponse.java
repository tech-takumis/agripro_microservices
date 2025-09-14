package com.hashjosh.application.dto;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;


// This dto is used to return the application details for a batch
@Getter
@Setter
@Data
@Builder
public class BatchApplicationResponse {
    private UUID applicationId; // Application Id
    private String applicationName; // Application Name
    private String fullName; // Name of the user who submitted the application
    private JsonNode dynamicFields; // Values
    private String status; // Status
}
