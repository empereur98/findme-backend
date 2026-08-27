package com.dhi.findme_backend.generator;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Agent de génération de validateur Bean Validation personnalisé
 * Génère automatiquement l'annotation et l'implémentation du validateur
 * en respectant le workflow de développement établi
 */
public class ValidatorGeneratorAgent {

    private static final String VALIDATION_PACKAGE = "com.dhi.findme_backend.validation";

    private final String validatorName;
    private final String fieldType;
    private final String errorMessage;
    private String validationLogic;

    public ValidatorGeneratorAgent(String validatorName, String fieldType, String errorMessage) {
        this.validatorName = validatorName;
        this.fieldType = fieldType;
        this.errorMessage = errorMessage;
        this.validationLogic = "// Validation logic here";
    }

    public ValidatorGeneratorAgent setValidationLogic(String validationLogic) {
        this.validationLogic = validationLogic;
        return this;
    }

    /**
     * Génère tous les fichiers selon le workflow
     * Étape 1: Annotation de validation
     * Étape 2: Implémentation du validateur
     * Étape 3: Tests du validateur
     */
    public void generate() throws IOException {
        System.out.println("🤖 Agent de génération de validateur démarré");
        System.out.println("📦 Validateur: " + validatorName);
        System.out.println("📋 Type: " + fieldType);
        System.out.println("");

        // Étape 1: Générer l'annotation
        generateAnnotation();
        System.out.println("✅ Annotation de validation générée");

        // Étape 2: Générer l'implémentation
        generateValidator();
        System.out.println("✅ Implémentation du validateur générée");

        // Étape 3: Générer les tests
        generateValidatorTests();
        System.out.println("✅ Tests du validateur générés");

        System.out.println("");
        System.out.println("🎉 Génération terminée avec succès");
    }

    private void generateAnnotation() throws IOException {
        String className = validatorName;
        String fileName = className + ".java";
        Path path = Paths.get("src/main/java/com/dhi/findme_backend/validation", fileName);

        StringBuilder content = new StringBuilder();
        content.append("package ").append(VALIDATION_PACKAGE).append(";\n\n");
        content.append("import jakarta.validation.Constraint;\n");
        content.append("import jakarta.validation.Payload;\n\n");
        content.append("import java.lang.annotation.*;\n\n");
        content.append("@Target({ElementType.FIELD})\n");
        content.append("@Retention(RetentionPolicy.RUNTIME)\n");
        content.append("@Constraint(validatedBy = ").append(className).append("Validator.class)\n");
        content.append("public @interface ").append(className).append(" {\n\n");
        content.append("    String message() default \"").append(errorMessage).append("\";\n\n");
        content.append("    Class<?>[] groups() default {};\n\n");
        content.append("    Class<? extends Payload>[] payload() default {};\n");
        content.append("}\n");

        Files.createDirectories(path.getParent());
        try (FileWriter writer = new FileWriter(path.toFile())) {
            writer.write(content.toString());
        }
    }

    private void generateValidator() throws IOException {
        String annotationName = validatorName;
        String validatorClassName = annotationName + "Validator";
        String fileName = validatorClassName + ".java";
        Path path = Paths.get("src/main/java/com/dhi/findme_backend/validation", fileName);

        StringBuilder content = new StringBuilder();
        content.append("package ").append(VALIDATION_PACKAGE).append(";\n\n");
        content.append("import jakarta.validation.ConstraintValidator;\n");
        content.append("import jakarta.validation.ConstraintValidatorContext;\n\n");
        content.append("public class ").append(validatorClassName)
                .append(" implements ConstraintValidator<").append(annotationName).append(", ")
                .append(fieldType).append("> {\n\n");
        content.append("    @Override\n");
        content.append("    public boolean isValid(").append(fieldType).append(" value, ")
                .append("ConstraintValidatorContext context) {\n");
        content.append("        if (value == null) {\n");
        content.append("            return true; // Let @NotNull handle null values\n");
        content.append("        }\n\n");
        content.append("        ").append(validationLogic).append("\n");
        content.append("    }\n");
        content.append("}\n");

        Files.createDirectories(path.getParent());
        try (FileWriter writer = new FileWriter(path.toFile())) {
            writer.write(content.toString());
        }
    }

    private void generateValidatorTests() throws IOException {
        String annotationName = validatorName;
        String validatorClassName = annotationName + "Validator";
        String fileName = validatorClassName + "Test.java";
        Path path = Paths.get("src/test/java/com/dhi/findme_backend/validation", fileName);

        StringBuilder content = new StringBuilder();
        content.append("package com.dhi.findme_backend.validation;\n\n");
        content.append("import jakarta.validation.ConstraintValidatorContext;\n");
        content.append("import org.junit.jupiter.api.Test;\n");
        content.append("import org.junit.jupiter.api.extension.ExtendWith;\n");
        content.append("import org.mockito.Mock;\n");
        content.append("import org.mockito.junit.jupiter.MockitoExtension;\n");
        content.append("import static org.junit.jupiter.api.Assertions.*;\n\n");
        content.append("@ExtendWith(MockitoExtension.class)\n");
        content.append("class ").append(validatorClassName).append("Test {\n\n");
        content.append("    @Mock\n");
        content.append("    private ConstraintValidatorContext context;\n\n");
        content.append("    private final ").append(validatorClassName).append(" validator = new ")
                .append(validatorClassName).append("();\n\n");
        content.append("    @Test\n");
        content.append("    void isValid_whenValidValue_shouldReturnTrue() {\n");
        content.append("        // Given\n");
        content.append("        ").append(fieldType).append(" value = getValue(); // Provide valid value\n\n");
        content.append("        // When\n");
        content.append("        boolean result = validator.isValid(value, context);\n\n");
        content.append("        // Then\n");
        content.append("        assertTrue(result);\n");
        content.append("    }\n\n");
        content.append("    @Test\n");
        content.append("    void isValid_whenInvalidValue_shouldReturnFalse() {\n");
        content.append("        // Given\n");
        content.append("        ").append(fieldType).append(" value = getInvalidValue(); // Provide invalid value\n\n");
        content.append("        // When\n");
        content.append("        boolean result = validator.isValid(value, context);\n\n");
        content.append("        // Then\n");
        content.append("        assertFalse(result);\n");
        content.append("    }\n\n");
        content.append("    @Test\n");
        content.append("    void isValid_whenNull_shouldReturnTrue() {\n");
        content.append("        // When\n");
        content.append("        boolean result = validator.isValid(null, context);\n\n");
        content.append("        // Then\n");
        content.append("        assertTrue(result);\n");
        content.append("    }\n\n");
        content.append("    // Helper methods to provide test values\n");
        content.append("    private ").append(fieldType).append(" getValue() {\n");
        content.append("        return null; // Implement according to field type\n");
        content.append("    }\n\n");
        content.append("    private ").append(fieldType).append(" getInvalidValue() {\n");
        content.append("        return null; // Implement according to field type\n");
        content.append("    }\n");
        content.append("}\n");

        Files.createDirectories(path.getParent());
        try (FileWriter writer = new FileWriter(path.toFile())) {
            writer.write(content.toString());
        }
    }

    public static void main(String[] args) throws IOException {
        // Exemple d'utilisation
        ValidatorGeneratorAgent agent = new ValidatorGeneratorAgent(
                "ValidPassword",
                "String",
                "Password must meet security requirements"
        ).setValidationLogic(
                "// Exemple de logique de validation de mot de passe\n" +
                "return value.length() >= 8 &&\n" +
                "       value.matches(\".*[A-Z].*\") &&\n" +
                "       value.matches(\".*[a-z].*\") &&\n" +
                "       value.matches(\".*[0-9].*\") &&\n" +
                "       value.matches(\".*[!@#$%^&*].*\");"
        );

        agent.generate();
    }
}
