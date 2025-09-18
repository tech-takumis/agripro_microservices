package com.hashjosh.document.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

@Configuration
@ConfigurationProperties(prefix = "app.file")
public class FileUploadProperties {
    private long maxFileSize = 5 * 1024 * 1024; // 5MB default
    private Set<String> allowedFileTypes = Set.of(
        "application/pdf",
        "image/jpeg",
        "image/png",
        "application/msword",  // .doc
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document"  // .docx
    );

    public long getMaxFileSize() {
        return maxFileSize;
    }

    public void setMaxFileSize(long maxFileSize) {
        this.maxFileSize = maxFileSize;
    }

    public Set<String> getAllowedFileTypes() {
        return allowedFileTypes;
    }

    public void setAllowedFileTypes(Set<String> allowedFileTypes) {
        this.allowedFileTypes = allowedFileTypes;
    }
}
