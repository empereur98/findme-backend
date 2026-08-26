---
description: Créer des tests spécifiques et intégraux pour contrôler le projet de manière complète
---

# Workflow: Tests spécifiques et intégraux

Ce workflow guide la création de tests spécifiques détectant les erreurs potentielles comme les NullPointerException, les erreurs de mapping, et les problèmes de validation.

## Étapes

1. **Créer des tests pour les Mappers**
   - Tester les cas null dans les expressions MapStruct
   - Tester les conversions Entity <-> DTO
   - Tester les champs optionnels et obligatoires
   - Tester les expressions personnalisées

2. **Créer des tests pour les Services**
   - Tester les cas d'erreur (null, empty, invalid)
   - Tester les exceptions et leur gestion
   - Tester les interactions avec les repositories
   - Tester la logique métier complexe

3. **Créer des tests pour les Controllers**
   - Tester les codes de statut HTTP
   - Tester les corps de réponse
   - Tester les validations des DTOs
   - Tester les scénarios d'erreur

4. **Créer des tests d'intégration**
   - Tester les flux complets (Controller -> Service -> Repository)
   - Tester avec une vraie base de données (Testcontainers)
   - Tester les transactions
   - Tester l'auditing JPA

5. **Créer des tests de configuration**
   - Tester que l'auditing JPA est activé
   - Tester que les beans sont correctement injectés
   - Tester les configurations de sécurité
   - Tester les configurations CORS

6. **Créer des tests de validation**
   - Tester toutes les annotations Bean Validation
   - Tester les messages d'erreur personnalisés
   - Tester les validateurs personnalisés
   - Tester les contraintes complexes

## Exemple d'utilisation

```
/create-comprehensive-tests
```

L'agent créera des tests spécifiques pour détecter les erreurs potentielles dans votre projet.
