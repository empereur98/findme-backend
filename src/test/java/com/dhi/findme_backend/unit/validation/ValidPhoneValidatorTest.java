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
class ValidPhoneValidatorTest {

    @Mock
    private ConstraintValidatorContext context;

    private ValidPhoneValidator validator;

    @BeforeEach
    void setUp() {
        validator = new ValidPhoneValidator();
    }

    @Test
    void isValid_whenNull_shouldReturnTrue() {
        assertTrue(validator.isValid(null, context));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "+237697000000",
            "+33 612345678",
            "+12345678901",
            "+44 7911123456",
            "+2250700000000"
    })
    void isValid_whenValidPhone_shouldReturnTrue(String phone) {
        assertTrue(validator.isValid(phone, context));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "",                     // Vide
            "697000000",            // Sans prÃ©fixe +
            "+",                    // Uniquement le +
            "+237",                 // Trop court (< 6 chiffres de numÃ©ro)
            "+1234567890123456789", // Trop long
            "+237ABCDEFGH",         // Lettres
            "++237697000000",       // Double +
            "+237 697 000 000"      // Multiples espaces non supportÃ©s par la regex
    })
    void isValid_whenInvalidPhone_shouldReturnFalse(String phone) {
        assertFalse(validator.isValid(phone, context));
    }
}

