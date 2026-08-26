package com.dhi.findme_backend.validation;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ValidPhoneValidatorTest {

    @Mock
    private ConstraintValidatorContext context;

    private final ValidPhoneValidator validator = new ValidPhoneValidator();

    @Test
    void isValid_whenValidValue_shouldReturnTrue() {
        // Given
        String value = getValue(); // Provide valid value

        // When
        boolean result = validator.isValid(value, context);

        // Then
        assertTrue(result);
    }

    @Test
    void isValid_whenInvalidValue_shouldReturnFalse() {
        // Given
        String value = getInvalidValue(); // Provide invalid value

        // When
        boolean result = validator.isValid(value, context);

        // Then
        assertFalse(result);
    }

    @Test
    void isValid_whenNull_shouldReturnTrue() {
        // When
        boolean result = validator.isValid(null, context);

        // Then
        assertTrue(result);
    }

    // Helper methods to provide test values
    private String getValue() {
        return "+221771234567"; // Valid phone number with country code
    }

    private String getInvalidValue() {
        return "invalid"; // Invalid phone number
    }
}
