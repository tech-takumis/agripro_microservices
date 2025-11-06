package com.hashjosh.document.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

@Configuration
@ConfigurationProperties(prefix = "app.file")
@Data
public class FileUploadProperties {
    private long maxFileSize = 20 * 1024 * 1024; // 20MB default
    private Set<String> allowedFileTypes = Set.of(
        "application/pdf",
        "image/jpeg",
        "image/png",
        "application/msword",  // .doc
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document"  // .docx
    );

}
