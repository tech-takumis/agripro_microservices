package com.hashjosh.application.dto.submission;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.hashjosh.application.model.Document;
import lombok.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationSubmissionDto {
        private UUID applicationTypeId;
        private Map<String, Object> fieldValues = new HashMap<>();
        private String coordinates;
        private UUID useId;
        private List<Document> documents;

        @JsonAnyGetter
        public Map<String, Object> getFieldValues() {
                return fieldValues;
        }

        @JsonAnySetter
        public void setFieldValue(String key, Object value) {
                this.fieldValues.put(key, value);
        }


}