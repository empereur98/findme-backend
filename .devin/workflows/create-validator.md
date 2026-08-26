---
description: Créer un validateur personnalisé Bean Validation
---

# Workflow: Créer un validateur personnalisé

Ce workflow guide la création d'un validateur Bean Validation personnalisé.

## Étapes

1. **Définir le besoin de validation**
   - Demander le nom du validateur (ex: @ValidPassword, @UniqueEmail)
   - Définir la règle de validation
   - Définir le message d'erreur

2. **Créer l'annotation de validation**
   - Créer le fichier dans `src/main/java/com/dhi/findme_backend/validation/`
   - Ajouter @Target({ElementType.FIELD})
   - Ajouter @Retention(RetentionPolicy.RUNTIME)
   - Ajouter @Constraint(validatedBy = ValidatorClass.class)
   - Définir message(), groups(), payload()

3. **Créer l'implémentation du validateur**
   - Créer la classe dans le même dossier
   - Implémenter ConstraintValidator<Annotation, Type>
   - Implémenter la méthode isValid()
   - Ajouter la logique de validation
   - Retourner true si valide, false sinon

4. **Créer les tests du validateur**
   - Créer le fichier de test dans `src/test/java/com/dhi/findme_backend/validation/`
   - Tester les cas valides
   - Tester les cas invalides
   - Tester les messages d'erreur

5. **Utiliser le validateur dans un DTO**
   - Ajouter l'annotation sur un champ de DTO
   - Vérifier que la validation fonctionne dans les tests

## Exemple d'utilisation

```
/create-validator
```

L'agent vous guidera dans la création du validateur personnalisé.
