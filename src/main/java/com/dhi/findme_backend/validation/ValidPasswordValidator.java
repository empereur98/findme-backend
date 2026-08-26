package com.dhi.findme_backend.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidPasswordValidator implements ConstraintValidator<ValidPassword, String> {

    private static final int MIN_PASSWORD_LENGTH = 8;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // Let @NotNull handle null values
        }

        return value.length() >= MIN_PASSWORD_LENGTH
                && value.matches(".*[A-Z].*")
                && value.matches(".*[a-z].*")
                && value.matches(".*[0-9].*")
                && value.matches(".*[!@#$%^&*].*");
    }
}