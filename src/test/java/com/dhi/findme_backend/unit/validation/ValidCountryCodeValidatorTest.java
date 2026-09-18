package com.dhi.findme_backend.unit.validation;

import com.dhi.findme_backend.validation.*;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class ValidCountryCodeValidatorTest {

    @Mock
    private ConstraintValidatorContext context;

    private ValidCountryCodeValidator validator;

    @BeforeEach
    void setUp() {
        validator = new ValidCountryCodeValidator();
    }

    @Test
    void isValid_whenNull_shouldReturnTrue() {
        assertTrue(validator.isValid(null, context));
    }

    @ParameterizedTest
    @ValueSource(strings = {"cm", "fr", "us", "ci", "sn", "ng", "de", "ca", "gb"})
    void isValid_whenValidCountryCode_shouldReturnTrue(String code) {
        assertTrue(validator.isValid(code, context));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "",      // Vide
            " ",     // Espace
            "CM",    // Majuscules (doit Ãªtre minuscule selon la regex)
            "Fr",    // Mixte
            "fra",   // 3 lettres ISO alpha-3
            "12",    // Chiffres
            "c",     // 1 seule lettre
            "cma",   // 3 lettres
            "c-",    // CaractÃ¨re spÃ©cial
            "cm "    // Avec espace Ã  la fin
    })
    void isValid_whenInvalidCountryCode_shouldReturnFalse(String code) {
        assertFalse(validator.isValid(code, context));
    }
}

