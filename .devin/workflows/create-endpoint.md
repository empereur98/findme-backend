---
description: Créer un nouveau endpoint REST complet (DTO, mapper, service, controller, tests)
---

# Workflow: Créer un nouveau endpoint REST

Ce workflow guide la création complète d'un endpoint REST suivant la méthodologie contract-first du projet.

## Étapes

1. **Définir le contrat OpenAPI**
   - Demander le chemin de l'endpoint (ex: /api/v1/users)
   - Demander la méthode HTTP (GET, POST, PUT, DELETE, PATCH)
   - Définir les schémas de requête/réponse
   - Définir les codes de réponse possibles

2. **Créer les DTOs**
   - Créer le DTO de requête dans `src/main/java/com/dhi/findme_backend/dto/`
   - Ajouter les annotations de validation Bean Validation
   - Créer le DTO de réponse dans le même dossier
   - Utiliser des records pour les DTOs immuables

3. **Créer le MapStruct mapper**
   - Créer l'interface dans `src/main/java/com/dhi/findme_backend/mapper/`
   - Ajouter l'annotation @Mapper(componentModel = "spring")
   - Définir les méthodes de mapping Entity <-> DTO
   - Gérer les cas particuliers avec @Mapping

4. **Créer l'interface service**
   - Créer l'interface dans `src/main/java/com/dhi/findme_backend/service/`
   - Définir les méthodes de l'API métier
   - Ajouter @TransactionalreadOnly = true pour les lectures

5. **Créer l'implémentation du service**
   - Créer la classe dans `src/main/java/com/dhi/findme_backend/service/impl/`
   - Implémenter l'interface service
   - Injecter le repository et le mapper
   - Ajouter la logique métier
   - Lever les exceptions appropriées (ResourceNotFoundException, BusinessException)

6. **Créer les tests unitaires du mapper**
   - Créer le fichier de test dans `src/test/java/com/dhi/findme_backend/mapper/`
   - Tester les cas null dans les expressions MapStruct
   - Tester les conversions Entity <-> DTO avec tous les champs
   - Tester les expressions personnalisées avec valeurs null
   - Tester les champs optionnels et obligatoires

7. **Créer les tests unitaires du service**
   - Créer le fichier de test dans `src/test/java/com/dhi/findme_backend/service/`
   - Utiliser @ExtendWith(MockitoExtension.class)
   - Mock les dépendances (repository, mapper)
   - Tester tous les cas: succès, erreurs, validations
   - Tester les cas null et empty
   - Tester les exceptions et leur gestion
   - Tester la logique métier complexe

8. **Créer le controller**
   - Créer la classe dans `src/main/java/com/dhi/findme_backend/controller/`
   - Ajouter @RestController et @RequestMapping
   - Injecter le service
   - Ajouter les annotations OpenAPI (@Operation, @ApiResponse)
   - Ajouter @Valid pour la validation des DTOs
   - Retourner les ResponseEntity appropriées

9. **Créer les tests d'intégration du controller**
   - Créer le fichier de test dans `src/test/java/com/dhi/findme_backend/controller/`
   - Utiliser @SpringBootTest(webEnvironment = RANDOM_PORT)
   - Utiliser Testcontainers pour PostgreSQL
   - Utiliser MockMvc ou WebTestClient
   - Tester tous les scénarios HTTP
   - Tester les codes de statut HTTP
   - Tester les corps de réponse
   - Tester les validations des DTOs

10. **Créer des tests d'intégration complets**
    - Tester les flux complets (Controller -> Service -> Repository)
    - Tester avec une vraie base de données (Testcontainers)
    - Tester les transactions
    - Tester l'auditing JPA
    - Tester les scénarios d'erreur de bout en bout

11. **Vérifier la gestion des erreurs**
    - Tester que le GlobalExceptionHandler retourne les bons codes
    - Vérifier le format des réponses d'erreur (RFC 7807)
    - Tester les exceptions personnalisées

12. **Mettre à jour la documentation OpenAPI**
    - Ajouter les annotations @Tag pour l'organisation
    - Documenter les paramètres et réponses
    - Vérifier que Swagger UI affiche correctement l'endpoint

## Exemple d'utilisation

```
/create-endpoint
```

L'agent vous guidera à travers chaque étape de la création de l'endpoint.
