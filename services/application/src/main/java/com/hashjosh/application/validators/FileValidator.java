package com.hashjosh.application.validators;

import com.fasterxml.jackson.databind.JsonNode;
import com.hashjosh.application.clients.DocumentServiceClient;
import com.hashjosh.application.dto.DocumentResponse;
import com.hashjosh.application.dto.ValidationErrors;
import com.hashjosh.application.model.ApplicationField;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class FileValidator implements ValidatorStrategy {

    private final DocumentServiceClient documentServiceClient;

    @Override
    public List<ValidationErrors> validate(ApplicationField field, JsonNode value) {
        List<ValidationErrors> errors = new ArrayList<>();
        if (value == null || !value.isTextual()) {
            errors.add(new ValidationErrors(
                    field.getKey(),
                    "Field must reference a file (use format file:<fieldKey>)"
            ));
        }
        return errors;
    }

    public String saveFile(JsonNode submittedValue,
                           Map<String, MultipartFile> files,
                           UUID applicationId,
                           String uploadedBy,
                           HttpServletRequest request) throws FileUploadException {

        if (submittedValue == null || submittedValue.isNull() || !submittedValue.isTextual()) {
            throw new FileUploadException("Submitted value must be string");
        }

        String token = request.getHeader("Authorization").substring(7);

        // Expect value like "file:crop_damage"
        String ref = submittedValue.asText().trim();
        if (!ref.startsWith("file:")) {
            throw new FileUploadException("Invalid file reference: " + ref);
        }

        String fieldKey = ref.substring(5); // crop_damage

        MultipartFile targetFile = files.get(fieldKey);
        if (targetFile == null) {
            throw new FileUploadException("No uploaded file provided for field " + fieldKey);
        }


        // Convert String to UUID;
        UUID uuidUploadedBy = UUID.fromString(uploadedBy);
        try {
            DocumentResponse response = documentServiceClient.uploadDocument(
                    token,
                    applicationId,
                    uuidUploadedBy,
                    targetFile
            );
            log.info("Uploaded file {} as object {}", targetFile.getOriginalFilename(), response.objectKey());
            return response.objectKey();
        } catch (Exception ex) {
            throw new FileUploadException("Could not upload file for field " + fieldKey, ex);
        }
    }
}

