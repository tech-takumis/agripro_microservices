package com.hashjosh.document.service;

import com.hashjosh.document.exception.FileValidationException;
import com.hashjosh.document.properties.FileUploadProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class FileValidationService {

    private final FileUploadProperties fileUploadProperties;

    public void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new FileValidationException("File cannot be empty");
        }

        // Check file size
        if (file.getSize() > fileUploadProperties.getMaxFileSize()) {
            throw new FileValidationException(
                String.format("File size %d bytes exceeds maximum allowed size of %d bytes",
                    file.getSize(),
                    fileUploadProperties.getMaxFileSize())
            );
        }

        // Check file type
        String fileContentType = file.getContentType();
        if (fileContentType == null) {
            throw new FileValidationException("Could not determine file type");
        }

        Set<String> allowedTypes = fileUploadProperties.getAllowedFileTypes();
        boolean isValidType = allowedTypes.stream()
                .anyMatch(allowedType -> fileContentType.toLowerCase().contains(allowedType.toLowerCase()));

        if (!isValidType) {
            throw new FileValidationException(
                String.format("File type '%s' is not allowed. Allowed types are: %s",
                    fileContentType,
                    String.join(", ", allowedTypes))
            );
        }

        // Check file extension
        String originalFilename = file.getOriginalFilename();
        if (originalFilename != null) {
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf('.') + 1).toLowerCase();
            String[] allowedExtensions = {"pdf", "jpg", "jpeg", "png", "doc", "docx"};
            
            boolean isValidExtension = false;
            for (String ext : allowedExtensions) {
                if (fileExtension.equals(ext)) {
                    isValidExtension = true;
                    break;
                }
            }
            
            if (!isValidExtension) {
                throw new FileValidationException(
                    String.format("File extension '.%s' is not allowed. Allowed extensions are: %s",
                        fileExtension,
                        String.join(", ", allowedExtensions))
                );
            }
        }
    }
}
