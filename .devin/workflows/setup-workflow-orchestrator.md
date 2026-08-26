---
description: Configurer un orchestrateur de workflows avec Temporal
---

# Workflow: Configuration de l'orchestrateur de workflows Temporal

Ce workflow guide l'implémentation de Temporal pour orchestrer les workflows métier dans l'application Spring Boot.

## Étapes

1. **Ajouter les dépendances Temporal**
   - Ajouter temporal-sdk au pom.xml
   - Ajouter temporal-spring-boot-starter si disponible
   - Configurer la version compatible avec Spring Boot 4.1.0

2. **Créer la configuration Temporal**
   - Créer TemporalConfiguration dans src/main/java/com/dhi/findme_backend/config/
   - Configurer le client Temporal
   - Configurer la connexion au serveur Temporal
   - Configurer les workers et task queues

3. **Créer les activités Temporal**
   - Créer les interfaces d'activités dans src/main/java/com/dhi/findme_backend/temporal/activity/
   - Implémenter les activités métier (création d'adresse, export, etc.)
   - Ajouter les annotations @ActivityInterface et @ActivityMethod
   - Gérer les exceptions et retries

4. **Créer les workflows Temporal**
   - Créer les interfaces de workflows dans src/main/java/com/dhi/findme_backend/temporal/workflow/
   - Définir les méthodes de workflow avec @WorkflowMethod
   - Définir les signaux avec @SignalMethod
   - Implémenter les workflows dans src/main/java/com/dhi/findme_backend/temporal/workflow/impl/

5. **Créer les workers Temporal**
   - Créer les workers dans src/main/java/com/dhi/findme_backend/temporal/worker/
   - Enregistrer les workflows et activités
   - Configurer les task queues
   - Démarrer les workers au démarrage de l'application

6. **Créer les endpoints pour déclencher les workflows**
   - Créer des controllers dans src/main/java/com/dhi/findme_backend/controller/
   - Ajouter les endpoints pour démarrer les workflows
   - Ajouter les endpoints pour interroger le statut des workflows
   - Ajouter les endpoints pour envoyer des signaux aux workflows

7. **Créer les tests des workflows**
   - Créer des tests unitaires pour les activités
   - Créer des tests d'intégration pour les workflows
   - Utiliser Temporal Test Environment pour les tests
   - Tester les scénarios d'erreur et retries

8. **Mettre à jour Docker Compose**
   - Ajouter le service Temporal Server
   - Configurer les ports et volumes
   - Configurer les dépendances avec PostgreSQL

9. **Documenter les workflows**
   - Documenter les workflows existants
   - Documenter les activités disponibles
   - Documenter les signaux et requêtes

## Exemple d'utilisation

```
/setup-workflow-orchestrator
```

L'agent vous guidera à travers chaque étape de la configuration de l'orchestrateur de workflows.
