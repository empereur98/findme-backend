# Workflows FindMe Backend

Ce dossier contient les workflows automatisés pour suivre la méthodologie de développement Spring Boot REST API définie dans `WORKFLOW_GUIDE.md`.

## Workflows disponibles

### `/create-entity`
Crée une nouvelle entité JPA avec son repository et la migration Flyway associée.

**Ce que fait le workflow :**
- Crée la classe d'entité (héritant de BaseEntity)
- Crée l'interface repository
- Crée la migration Flyway
- Crée les tests du repository (si requis)

**Quand l'utiliser :** Lorsque vous avez besoin d'une nouvelle table dans la base de données.

---

### `/create-endpoint`
Crée un endpoint REST complet suivant l'approche contract-first.

**Ce que fait le workflow :**
- Définit le contrat OpenAPI
- Crée les DTOs de requête/réponse
- Crée le MapStruct mapper
- Crée l'interface service et son implémentation
- Crée les tests unitaires du service
- Crée le controller avec annotations OpenAPI
- Crée les tests d'intégration du controller
- Vérifie la gestion des erreurs

**Quand l'utiliser :** Pour chaque nouvelle fonctionnalité API (GET, POST, PUT, DELETE, PATCH).

---

### `/run-tests`
Exécute les tests du projet en suivant la pyramide de tests.

**Ce que fait le workflow :**
- Exécute les tests unitaires
- Exécute les tests d'intégration avec Testcontainers
- Génère le rapport de couverture JaCoCo
- (Optionnel) Analyse la qualité avec SonarQube

**Quand l'utiliser :** Avant de committer du code ou pour vérifier que tout fonctionne.

---

### `/create-validator`
Crée un validateur Bean Validation personnalisé.

**Ce que fait le workflow :**
- Crée l'annotation de validation
- Crée l'implémentation du validateur
- Crée les tests du validateur
- Montre comment l'utiliser dans un DTO

**Quand l'utiliser :** Lorsque vous avez besoin d'une règle de validation complexe non couverte par les annotations standard.

---

### `/setup-workflow-orchestrator`
Configure un orchestrateur de workflows avec Temporal.

**Ce que fait le workflow :**
- Ajoute les dépendances Temporal au pom.xml
- Crée la configuration Temporal
- Crée les activités Temporal pour les opérations métier
- Crée les workflows Temporal pour orchestrer les processus
- Crée les workers Temporal pour exécuter les workflows
- Met à jour Docker Compose avec le serveur Temporal
- Crée les tests des workflows

**Quand l'utiliser :** Lorsque vous avez besoin d'orchestrer des processus métier complexes avec gestion d'état, retries et long-running workflows.

---

### `/create-comprehensive-tests`
Crée des tests spécifiques et intégraux pour contrôler le projet de manière complète.

**Ce que fait le workflow :**
- Crée des tests pour les Mappers (cas null, expressions personnalisées)
- Crée des tests pour les Services (cas d'erreur, exceptions, logique métier)
- Crée des tests pour les Controllers (codes HTTP, validations, scénarios d'erreur)
- Crée des tests d'intégration (flux complets, Testcontainers, transactions)
- Crée des tests de configuration (auditing JPA, beans, sécurité)
- Crée des tests de validation (Bean Validation, validateurs personnalisés)

**Quand l'utiliser :** Pour détecter les erreurs potentielles comme NullPointerException, erreurs de mapping, et problèmes de validation.

---

### `/setup-security`
Configure Spring Security avec JWT pour l'authentification.

**Ce que fait le workflow :**
- Configure SecurityFilterChain
- Crée le JWT filter
- Crée le JWT utility
- Crée l'endpoint d'authentification
- Crée les tests de sécurité
- Met à jour OpenAPI avec le schéma de sécurité

**Quand l'utiliser :** Une seule fois au début du projet pour mettre en place l'authentification.

---

### `/setup-config`
Configure l'application Spring Boot avec les propriétés et configurations de base.

**Ce que fait le workflow :**
- Configure les propriétés de base (nom, port, contexte)
- Configure la base de données PostgreSQL
- Configure les profils (dev, test, prod)
- Configure la journalisation (Logback/Log4j2)
- Configure OpenAPI/Swagger
- Configure CORS
- Configure les tests et JaCoCo

**Quand l'utiliser :** Une seule fois au début du projet pour configurer l'application.

---

## Comment utiliser les workflows

Dans votre IDE avec l'agent Cascade, tapez simplement :

```
/nom-du-workflow
```

Par exemple :
```
/create-entity
```

L'agent vous guidera à travers chaque étape du workflow et créera automatiquement les fichiers nécessaires.

## Ordre recommandé pour un nouveau projet

1. `/setup-config` - Configurer l'application Spring Boot
2. `/setup-security` - Configurer l'authentification
3. `/create-entity` - Créer les entités nécessaires
4. `/create-endpoint` - Créer les endpoints pour chaque fonctionnalité
5. `/setup-workflow-orchestrator` - Configurer l'orchestrateur de workflows (si nécessaire)
6. `/create-comprehensive-tests` - Créer des tests spécifiques pour détecter les erreurs
7. `/run-tests` - Vérifier que tout fonctionne

## Personnalisation

Vous pouvez modifier ces fichiers de workflow pour adapter les étapes à vos besoins spécifiques. Chaque workflow est documenté avec les étapes qu'il suit.
