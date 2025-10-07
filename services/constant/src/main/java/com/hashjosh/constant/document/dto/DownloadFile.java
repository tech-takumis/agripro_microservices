package com.hashjosh.constant.document.dto;

public record DownloadFile(
        String fileName,
        String fileType,
        byte[] fileData
) {
    public String getContentDisposition() {
        return "attachment; filename=\"" + fileName + "\"";
    }
}
