package com.hashjosh.application.dto;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationSubmissionDto {
        private UUID applicationTypeId;
        private UUID uploadedBy;
        private Map<String, Object> fieldValues = new HashMap<>();
        @JsonAnyGetter
        public Map<String, Object> getFieldValues() {
                return fieldValues;
        }
}