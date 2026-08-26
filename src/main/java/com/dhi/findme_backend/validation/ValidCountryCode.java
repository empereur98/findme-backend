package com.dhi.findme_backend.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidCountryCodeValidator.class)
public @interface ValidCountryCode {

    String message() default "Le code pays doit être au format ISO 3166-1 alpha-2 (ex: sn, ci, cm)";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}