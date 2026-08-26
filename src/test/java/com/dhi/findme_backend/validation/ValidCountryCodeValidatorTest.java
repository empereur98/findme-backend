package com.dhi.findme_backend.validation;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ValidCountryCodeValidatorTest {

    @Mock
    private ConstraintValidatorContext context;

    private final ValidCountryCodeValidator validator = new ValidCountryCodeValidator();

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
        return "sn"; // Valid country code (Senegal) - must be lowercase
    }

    private String getInvalidValue() {
        return "INVALID"; // Invalid country code - too long
    }
}
