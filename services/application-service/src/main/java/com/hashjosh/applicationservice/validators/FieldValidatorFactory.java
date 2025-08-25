package com.hashjosh.applicationservice.validators;

import com.hashjosh.applicationservice.enums.FieldType;
import com.hashjosh.applicationservice.exceptions.ApplicationNotSupportedDatatypeException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class FieldValidatorFactory {


    private final Map<String, ValidatorStrategy> strategies = new HashMap<>();
    @Autowired
    public FieldValidatorFactory(
            BooleanValidator booleanValidator,
            DateValidator dateValidator,
            FileValidator fileValidator,
            LocationValidator locationValidator,
            NumberValidator numberValidator,
            SelectValidator selectValidator,
            SignatureValidator signatureValidator,
            TextValidator textValidator,
            MultiSelectValidator multiSelectValidator
    ) {
        strategies.put(FieldType.BOOLEAN.name(), booleanValidator);
        strategies.put(FieldType.DATE.name(), dateValidator);
        strategies.put(FieldType.FILE.name(), fileValidator);
        strategies.put(FieldType.LOCATION.name(), locationValidator);
        strategies.put(FieldType.NUMBER.name(), numberValidator);
        strategies.put(FieldType.SELECT.name(), selectValidator);
        strategies.put(FieldType.SIGNATURE.name(), signatureValidator);
        strategies.put(FieldType.TEXT.name(), textValidator);
        strategies.put(FieldType.MULTI_SELECT.name(), multiSelectValidator);
    }

    public ValidatorStrategy getStrategy(String fieldType){
        ValidatorStrategy strategy = strategies.get(fieldType);

        if(strategy == null){
            throw new ApplicationNotSupportedDatatypeException("Unsupported datatype:: "+fieldType);
        }
        return strategy;
    }

}
