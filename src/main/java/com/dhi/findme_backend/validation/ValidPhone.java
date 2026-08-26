package com.dhi.findme_backend.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidPhoneValidator.class)
public @interface ValidPhone {

    String message() default "Le numéro de téléphone doit être au format international (ex: +221 77 123 45 67)";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}