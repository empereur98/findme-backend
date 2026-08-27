package com.dhi.findme_backend.generator;

import java.io.IOException;

public class RunAllValidators {
    public static void main(String[] args) throws IOException {
        System.out.println("🚀 Lancement de tous les générateurs de validateurs\n");

        // Validateur de mot de passe
        System.out.println("=== Génération du validateur de mot de passe ===");
        ValidatorGeneratorAgent passwordValidator = new ValidatorGeneratorAgent(
            "ValidPassword",
            "String",
            "Le mot de passe doit contenir au moins 8 caractères, une majuscule, une minuscule, un chiffre et un caractère spécial"
        ).setValidationLogic(
            "return value != null && value.length() >= 8 &&\n" +
            "       value.matches(\".*[A-Z].*\") &&\n" +
            "       value.matches(\".*[a-z].*\") &&\n" +
            "       value.matches(\".*[0-9].*\") &&\n" +
            "       value.matches(\".*[!@#$%^&*].*\");"
        );
        passwordValidator.generate();
        System.out.println();

        // Validateur de téléphone
        System.out.println("=== Génération du validateur de téléphone ===");
        ValidatorGeneratorAgent phoneValidator = new ValidatorGeneratorAgent(
            "ValidPhone",
            "String",
            "Le numéro de téléphone doit être au format international (ex: +221 77 123 45 67)"
        ).setValidationLogic(
            "return value != null && value.matches(\"^\\\\+[0-9]{1,3}[\\\\s]?[0-9]{6,14}$\");"
        );
        phoneValidator.generate();
        System.out.println();

        // Validateur de code pays
        System.out.println("=== Génération du validateur de code pays ===");
        ValidatorGeneratorAgent countryCodeValidator = new ValidatorGeneratorAgent(
            "ValidCountryCode",
            "String",
            "Le code pays doit être au format ISO 3166-1 alpha-2 (ex: sn, ci, cm)"
        ).setValidationLogic(
            "return value != null && value.matches(\"^[a-z]{2}$\");"
        );
        countryCodeValidator.generate();
        System.out.println();

        System.out.println("🎉 Tous les validateurs ont été générés avec succès!");
    }
}
