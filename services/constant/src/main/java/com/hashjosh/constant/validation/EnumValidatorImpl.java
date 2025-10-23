package com.hashjosh.constant.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;

public class EnumValidatorImpl implements ConstraintValidator<EnumValidator, Object> {

    private Class<? extends Enum<?>> enumClass;

    @Override
    public void initialize(EnumValidator annotation) {
        this.enumClass = annotation.enumClass();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) return true; // optional: allow nulls

        if (enumClass.isEnum()) {
            Object[] enumValues = enumClass.getEnumConstants();
            if (value instanceof Enum<?>) {
                // if it's already an enum instance
                return Arrays.asList(enumValues).contains(value);
            } else if (value instanceof String strValue) {
                // if it's a string, try to match by name
                return Arrays.stream(enumValues)
                        .map(Object::toString)
                        .anyMatch(enumVal -> enumVal.equalsIgnoreCase(strValue));
            }
        }
        return false;
    }
}

