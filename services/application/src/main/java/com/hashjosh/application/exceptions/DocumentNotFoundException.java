package com.hashjosh.application.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class DocumentNotFoundException extends  RuntimeException{
    public DocumentNotFoundException(String message) {
        super(message);
    }
}
