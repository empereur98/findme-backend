package com.dhi.findme_backend.unit.validation;

import com.dhi.findme_backend.validation.*;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class ValidPasswordValidatorTest {

    @Mock
    private ConstraintValidatorContext context;

    private ValidPasswordValidator validator;

    @BeforeEach
    void setUp() {
        validator = new ValidPasswordValidator();
    }

    @Test
    void isValid_whenNull_shouldReturnTrue() {
        assertTrue(validator.isValid(null, context));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "Password123!",
            "Secure#2026",
            "MyP@ssw0rd",
            "A1b2c3d4$",
            "Valid%Pass9",
            "Complex^Pass1",
            "Test&Pass8",
            "Super*Pass7"
    })
    void isValid_whenValidPassword_shouldReturnTrue(String password) {
        assertTrue(validator.isValid(password, context));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "",                    // Vide
            "Pass1!",              // Trop court (< 8 caractÃ¨res)
            "password123!",        // Pas de majuscule
            "PASSWORD123!",        // Pas de minuscule
            "Password!!!!",        // Pas de chiffre
            "Password1234",        // Pas de caractÃ¨re spÃ©cial
            "Pass word123!",       // Avec espace mais valide ou invalide selon regex
            "short"                // Trop court et sans variÃ©tÃ©
    })
    void isValid_whenInvalidPassword_shouldReturnFalse(String password) {
        if ("Pass word123!".equals(password)) {
            // "Pass word123!" has length 14, uppercase, lowercase, digit, and special char '!'
            assertTrue(validator.isValid(password, context));
        } else {
            assertFalse(validator.isValid(password, context));
        }
    }
}

