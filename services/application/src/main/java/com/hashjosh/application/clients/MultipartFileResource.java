package com.hashjosh.application.clients;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public class MultipartFileResource extends ByteArrayResource {
    private final String filename;
    private final String contentType;

    public MultipartFileResource(MultipartFile multipartFile) throws IOException {
        super(multipartFile.getBytes()); // fully materialize in memory
        this.filename = multipartFile.getOriginalFilename();
        this.contentType = multipartFile.getContentType();
    }

    @Override
    public String getFilename() {
        return this.filename;
    }

    @Override
    public String getDescription() {
        return this.contentType != null ? this.contentType : super.getDescription();
    }
}