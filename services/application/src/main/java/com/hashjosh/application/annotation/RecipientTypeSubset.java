package com.hashjosh.application.annotation;


import com.hashjosh.constant.application.RecipientType;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = RecipientTypeValidator.class)
@Target({ElementType.FIELD, ElementType.TYPE_PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface RecipientTypeSubset {
    RecipientType[] anyOf();
    String message() default "Invalid recipient type provided. (Supported types: Agriculture, PCIC)";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

}
