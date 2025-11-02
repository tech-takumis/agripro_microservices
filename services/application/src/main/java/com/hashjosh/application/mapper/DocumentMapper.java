package com.hashjosh.application.mapper;

import com.hashjosh.application.dto.document.DocumentRequest;
import com.hashjosh.application.model.Document;
import com.hashjosh.constant.document.dto.DocumentResponse;
import org.springframework.stereotype.Component;

@Component
public class DocumentMapper {


    public Document toDocument(DocumentRequest request){
        return Document.builder()
                .documentId(request.getDocumentId())
                .fileName(request.getFileName())
                .fileType(request.getFileType())
                .coordinates(request.getCoordinates())
                .coordinates(request.getCoordinates())
                .build();
    }


}
