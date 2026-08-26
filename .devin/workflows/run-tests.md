---
description: Exécuter les tests unitaires et d'intégration avec Maven
---

# Workflow: Exécuter les tests

Ce workflow exécute les tests du projet en suivant la pyramide de tests définie.

## Étapes

1. **Exécuter les tests unitaires**
   ```bash
   ./mvnw test
   ```
   - Exécute uniquement les tests unitaires
   - Utilise des mocks (Mockito)
   - Rapide (< 1 minute)

2. **Exécuter les tests d'intégration**
   ```bash
   ./mvnw verify
   ```
   - Exécute tous les tests (unitaires + intégration)
   - Utilise Testcontainers pour PostgreSQL
   - Plus lent mais plus complet

3. **Vérifier la couverture de code**
   ```bash
   ./mvnw jacoco:report
   ```
   - Génère le rapport JaCoCo
   - Vérifie que la couverture est ≥ 80%
   - Rapport disponible dans `target/site/jacoco/index.html`

4. **Analyser la qualité du code** (optionnel)
   ```bash
   ./mvnw sonar:sonar
   ```
   - Nécessite un serveur SonarQube configuré
   - Analyse la qualité du code
   - Vérifie les Quality Gates

## Exemple d'utilisation

```
/run-tests
```

L'agent exécutera les tests et vous rapportera les résultats.
