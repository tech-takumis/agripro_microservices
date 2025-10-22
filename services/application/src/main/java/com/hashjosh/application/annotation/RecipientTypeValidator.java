package com.hashjosh.application.annotation;

import com.hashjosh.constant.application.RecipientType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;

public class RecipientTypeValidator implements ConstraintValidator<RecipientTypeSubset, RecipientType> {

    private RecipientType[] subset;
    @Override
    public void initialize(RecipientTypeSubset constraint) {
        this.subset = constraint.anyOf();
    }

    @Override
    public boolean isValid(RecipientType value, ConstraintValidatorContext constraintValidatorContext) {
        return value == null | Arrays.asList(subset).contains(value);
    }
}
