package com.hashjosh.applicationservice.validators;

import com.hashjosh.applicationservice.dto.ValidationErrors;
import com.fasterxml.jackson.databind.JsonNode;
import com.hashjosh.applicationservice.model.ApplicationFields;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class FileValidator implements ValidatorStrategy{
    private static Logger logger = LoggerFactory.getLogger(FileValidator.class);
    private static final Path UPLOAD_DIR = Path.of("uploads");

    static {
        try{
            Files.createDirectories(UPLOAD_DIR);
        }catch (IOException ex){
            throw  new RuntimeException("Unable to create upload directory "+ex);
        }
    }

    @Override
    public List<ValidationErrors> validate(ApplicationFields field, JsonNode value) {
        List<ValidationErrors> errors = new ArrayList<>();
        if(value == null || !value.isTextual()){
           errors.add(new ValidationErrors(
                   field.getFieldName(),
                   "Field must be a file name (FILE)"
           ));
        }

        return errors;
    }

    /**
     *
     * @param submittedValue the json node containing the filename submitted in the request
     * @param files list of uploaded MultipartFiles
     * @return the store file path (realtive URL)
     */
    public static String saveFile(JsonNode submittedValue, List<MultipartFile> files) throws FileUploadException {
        logger.info("File saved to "+UPLOAD_DIR);

        if(submittedValue == null || !submittedValue.isTextual()){
            throw new FileUploadException("Submitted file value must be string");
        }

        String originalFileName = submittedValue.asText().trim();
        if(originalFileName.isEmpty()){
            throw new FileUploadException("Submitted file name cannot be empty");
        }

        MultipartFile targetFile = files.stream()
                .filter(f -> originalFileName.equals(f.getOriginalFilename()))
                .findFirst()
                .orElseThrow(() -> new FileUploadException("Could not find file "+originalFileName));

        try{
            String uniqueFileName = UUID.randomUUID() + "-" + sanitizeFileName(originalFileName);
            Path destination = UPLOAD_DIR.resolve(uniqueFileName);
            Files.copy(targetFile.getInputStream(),destination, StandardCopyOption.REPLACE_EXISTING);

            return "/uploads/"+uniqueFileName;
        } catch (IOException e) {
            throw new FileUploadException("Could not save file "+originalFileName,e);
        }
    }

    /**
     *
     * Ensure the filename does not include any  invalid path character
     */
    public static String sanitizeFileName(String originalFileName) {
        return originalFileName.replaceAll("[^a-zA-Z0-9]", "_");
    }

}
