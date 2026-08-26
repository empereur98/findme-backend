# Agents d'Automatisation de Production - FindMe Backend

Ce document décrit les agents d'automatisation mis en place pour le processus de production du projet FindMe Backend.

## Table des matières

1. [Agent CI/CD](#agent-cicd)
2. [Agent de Déploiement](#agent-de-déploiement)
3. [Agent de Monitoring](#agent-de-monitoring)
4. [Agent de Migration](#agent-de-migration)
5. [Agent de Sauvegarde](#agent-de-sauvegarde)
6. [Agent de Vérification de Santé](#agent-de-vérification-de-santé)

---

## Agent CI/CD

**Fichier**: `.github/workflows/ci-cd.yml`

### Description
Pipeline GitHub Actions qui automatise le processus d'intégration continue et de déploiement continu.

### Fonctionnalités

#### Étape 1: Build et Tests Unitaires
- Compile le projet avec Maven
- Exécute les tests unitaires
- Génère un rapport de tests

#### Étape 2: Tests d'Intégration
- Utilise Testcontainers avec PostgreSQL
- Exécute les tests d'intégration
- Vérifie la compatibilité avec la base de données

#### Étape 3: Analyse de Qualité (SonarQube)
- Analyse la qualité du code
- Vérifie les règles de codage
- Génère des métriques de qualité

#### Étape 4: Couverture de Code
- Génère le rapport JaCoCo
- Envoie les résultats à Codecov
- Vérifie que la couverture ≥ 80%

#### Étape 5: Build Docker Image
- Construit l'image Docker
- Pousse l'image vers Docker Hub
- Tag automatique selon la branche

#### Étape 6: Déploiement Staging
- Déploie sur l'environnement de staging
- Exécute les tests de fumée
- Notifie l'équipe via Slack

#### Étape 7: Déploiement Production
- Crée une sauvegarde automatique
- Déploie sur l'environnement de production
- Exécute les tests de fumée
- Rollback automatique en cas d'échec

### Configuration requise

Ajouter les secrets GitHub suivants:
- `SONAR_TOKEN`: Token SonarQube
- `DOCKER_USERNAME`: Utilisateur Docker Hub
- `DOCKER_PASSWORD`: Mot de passe Docker Hub
- `STAGING_HOST`: Hôte de staging
- `STAGING_USER`: Utilisateur SSH staging
- `STAGING_SSH_KEY`: Clé SSH staging
- `PRODUCTION_HOST`: Hôte de production
- `PRODUCTION_USER`: Utilisateur SSH production
- `PRODUCTION_SSH_KEY`: Clé SSH production
- `SLACK_WEBHOOK`: Webhook Slack pour notifications

### Déclenchement

Le pipeline se déclenche automatiquement sur:
- Push vers `main` ou `develop`
- Pull Request vers `main` ou `develop`

---

## Agent de Déploiement

**Fichiers**: 
- `Dockerfile`
- `docker-compose.yml`
- `.dockerignore`

### Description
Configuration Docker pour le déploiement en production avec orchestration multi-conteneurs.

### Fonctionnalités

#### Dockerfile
- Build multi-stage pour optimiser la taille
- Utilisation de JRE Alpine pour la production
- Configuration JVM optimisée pour les conteneurs
- Health check intégré

#### Docker Compose
- Orchestration de plusieurs services:
  - **backend**: Application Spring Boot
  - **postgres**: Base de données PostgreSQL
  - **prometheus**: Collecte de métriques
  - **grafana**: Visualisation des métriques
- Configuration des volumes persistants
- Configuration des réseaux
- Health checks pour tous les services

### Utilisation

```bash
# Démarrer tous les services
docker-compose up -d

# Arrêter tous les services
docker-compose down

# Voir les logs
docker-compose logs -f backend

# Redémarrer un service
docker-compose restart backend
```

### Variables d'environnement

Créer un fichier `.env` avec:
```env
SPRING_PROFILES_ACTIVE=prod
DATABASE_URL=jdbc:postgresql://postgres:5432/findme
DATABASE_USERNAME=findme
DATABASE_PASSWORD=findme
SECURITY_USER=admin
SECURITY_PASSWORD=admin
CORS_ALLOWED_ORIGINS=https://findme.com
POSTGRES_DB=findme
POSTGRES_USER=findme
POSTGRES_PASSWORD=findme
GRAFANA_ADMIN_USER=admin
GRAFANA_ADMIN_PASSWORD=admin
```

---

## Agent de Monitoring

**Fichiers**:
- `prometheus/prometheus.yml`
- `grafana/provisioning/datasources/prometheus.yml`
- `grafana/provisioning/dashboards/dashboards.yml`
- `src/main/java/com/dhi/findme_backend/config/MetricsConfig.java`

### Description
Système de monitoring complet avec Prometheus et Grafana pour surveiller l'application en production.

### Fonctionnalités

#### Prometheus
- Collecte des métriques Spring Boot Actuator
- Scrape toutes les 15 secondes
- Stockage des données temporelles
- Configuration des alertes (à ajouter)

#### Grafana
- Dashboard automatiquement provisionnés
- Visualisation des métriques en temps réel
- Alertes et notifications
- Configuration datasource Prometheus

#### Métriques Spring Boot
- Métriques JVM (mémoire, threads, CPU)
- Métriques HTTP (requêtes, temps de réponse)
- Métriques de base de données
- Métriques personnalisées

### Accès

- **Prometheus**: http://localhost:9090
- **Grafana**: http://localhost:3000 (admin/admin)

### Configuration des métriques

Ajouter des métriques personnalisées dans `MetricsConfig.java`:

```java
@Bean
public MeterRegistryCustomizer<MeterRegistry> metricsCommonTags() {
    return registry -> registry.config().commonTags(
            "application", "findme-backend",
            "region", System.getenv().getOrDefault("REGION", "default")
    );
}
```

---

## Agent de Migration

**Fichiers**:
- `src/main/java/com/dhi/findme_backend/migration/MigrationAgent.java`
- `src/main/java/com/dhi/findme_backend/migration/MigrationController.java`
- `src/main/java/com/dhi/findme_backend/migration/MigrationException.java`

### Description
Agent automatisé pour gérer les migrations de base de données Flyway avec API REST pour le contrôle.

### Fonctionnalités

#### MigrationAgent
- Exécution automatique des migrations
- Vérification de l'état des migrations
- Validation des migrations
- Réparation en cas d'échec
- Nettoyage (développement uniquement)

#### MigrationController
Endpoints REST pour gérer les migrations:
- `POST /api/v1/admin/migrations/migrate` - Exécuter les migrations
- `GET /api/v1/admin/migrations/status` - État des migrations
- `POST /api/v1/admin/migrations/validate` - Valider les migrations
- `POST /api/v1/admin/migrations/repair` - Réparer les migrations

### Activation

Activer l'agent dans `application.yml`:
```yaml
migration:
  agent:
    enabled: true
```

### Utilisation

```bash
# Via l'API REST
curl -X POST http://localhost:8080/api/v1/admin/migrations/migrate \
  -H "Authorization: Bearer <token>"

# Via Maven (alternative)
./mvnw flyway:migrate
```

### Sécurité

Les endpoints de migration sont protégés par JWT et nécessitent un rôle d'administrateur.

---

## Agent de Sauvegarde

**Fichier**: `scripts/backup.sh`

### Description
Script automatisé pour créer des sauvegardes complètes avant chaque déploiement.

### Fonctionnalités

- Sauvegarde de la base de données PostgreSQL
- Sauvegarde des fichiers de configuration
- Sauvegarde des logs
- Nettoyage automatique des anciennes sauvegardes (garde les 10 dernières)
- Horodatage automatique des sauvegardes

### Utilisation

```bash
# Exécuter une sauvegarde manuelle
./scripts/backup.sh

# Le script est automatiquement exécuté par le pipeline CI/CD avant déploiement
```

### Emplacement des sauvegardes

Par défaut: `/opt/findme-backups/`

Structure:
```
/opt/findme-backups/
├── findme-backup-20240120_143022-database.sql
├── findme-backup-20240120_143022-config.tar.gz
├── findme-backup-20240120_143022-logs.tar.gz
└── ...
```

### Configuration

Variables d'environnement:
- `BACKUP_DIR`: Répertoire de sauvegarde (défaut: `/opt/findme-backups`)

---

## Agent de Vérification de Santé

**Fichier**: `scripts/health-check.sh`

### Description
Script qui vérifie que l'application est fonctionnelle après déploiement.

### Fonctionnalités

- Vérification du endpoint `/actuator/health`
- Vérification du endpoint `/actuator/info`
- Vérification du endpoint `/actuator/metrics`
- Rétries automatiques avec intervalle configurable
- Timeout configurable
- Rapport détaillé de l'état

### Utilisation

```bash
# Exécuter manuellement
./scripts/health-check.sh

# Avec configuration personnalisée
HEALTH_URL=http://localhost:8080/actuator/health \
MAX_RETRIES=30 \
RETRY_INTERVAL=5 \
TIMEOUT=10 \
./scripts/health-check.sh
```

### Configuration

Variables d'environnement:
- `HEALTH_URL`: URL de health check (défaut: `http://localhost:8080/actuator/health`)
- `MAX_RETRIES`: Nombre maximum de tentatives (défaut: 30)
- `RETRY_INTERVAL`: Intervalle entre tentatives en secondes (défaut: 5)
- `TIMEOUT`: Timeout en secondes (défaut: 10)

---

## Agent de Rollback

**Fichier**: `scripts/rollback.sh`

### Description
Script qui restaure la version précédente en cas d'échec de déploiement.

### Fonctionnalités

- Arrêt des services
- Restauration de la base de données depuis la sauvegarde
- Restauration de la configuration
- Redémarrage des services
- Vérification de santé automatique
- Confirmation avant exécution

### Utilisation

```bash
# Exécuter un rollback
./scripts/rollback.sh

# Le script est automatiquement exécuté par le pipeline CI/CD en cas d'échec
```

### Configuration

Variables d'environnement:
- `BACKUP_DIR`: Répertoire des sauvegardes (défaut: `/opt/findme-backups`)

---

## Agent de Tests de Fumée

**Fichier**: `scripts/smoke-tests.sh`

### Description
Script qui exécute des tests de base après déploiement pour vérifier le bon fonctionnement.

### Fonctionnalités

- Tests des endpoints Actuator
- Tests des endpoints API (à configurer)
- Rapport de résultats
- Échec si un test échoue

### Utilisation

```bash
# Exécuter les tests de fumée
./scripts/smoke-tests.sh

# Avec URL personnalisée
API_URL=https://api.staging.findme.com \
API_VERSION=v1 \
./scripts/smoke-tests.sh
```

### Configuration

Variables d'environnement:
- `API_URL`: URL de l'API (défaut: `http://localhost:8080`)
- `API_VERSION`: Version de l'API (défaut: `v1`)

### Personnalisation

Ajouter vos propres tests dans le script:

```bash
run_test "List Users" "/api/${API_VERSION}/users" "200"
run_test "Get User" "/api/${API_VERSION}/users/1" "200"
```

---

## Workflow Complet de Déploiement

### Déploiement Staging

1. **Push sur branche `develop`**
2. **Agent CI/CD** se déclenche
3. **Build et tests** s'exécutent
4. **Image Docker** construite et poussée
5. **Agent de déploiement** déploie sur staging
6. **Agent de vérification de santé** vérifie l'application
7. **Agent de tests de fumée** exécute les tests
8. **Notification Slack** envoyée

### Déploiement Production

1. **Push sur branche `main`**
2. **Agent CI/CD** se déclenche
3. **Build et tests** s'exécutent
4. **Image Docker** construite et poussée
5. **Agent de sauvegarde** crée une sauvegarde
6. **Agent de déploiement** déploie sur production
7. **Agent de migration** exécute les migrations
8. **Agent de vérification de santé** vérifie l'application
9. **Agent de tests de fumée** exécute les tests
10. **Notification Slack** envoyée
11. **En cas d'échec**: Agent de rollback automatique

---

## Maintenance

### Mise à jour des agents

1. Modifier les fichiers correspondants
2. Tester localement
3. Commit et push
4. Le pipeline CI/CD déploiera automatiquement

### Surveillance

- **Grafana**: Surveiller les métriques en temps réel
- **Prometheus**: Interroger les métriques via PromQL
- **Logs**: Consulter les logs dans `logs/`

### Dépannage

#### Problème de déploiement
```bash
# Vérifier les logs
docker-compose logs backend

# Vérifier la santé
./scripts/health-check.sh

# Rollback si nécessaire
./scripts/rollback.sh
```

#### Problème de migration
```bash
# Vérifier l'état
curl http://localhost:8080/api/v1/admin/migrations/status

# Réparer
curl -X POST http://localhost:8080/api/v1/admin/migrations/repair
```

#### Problème de monitoring
```bash
# Vérifier Prometheus
curl http://localhost:9090/api/v1/targets

# Vérifier Grafana
curl http://localhost:3000/api/health
```

---

## Sécurité

### Bonnes pratiques

- Ne jamais committer les secrets dans le code
- Utiliser des secrets GitHub pour les données sensibles
- Changer régulièrement les mots de passe
- Limiter l'accès aux endpoints d'administration
- Utiliser HTTPS en production
- Activer le firewall

### Audit

- Les logs sont conservés 30 jours
- Les sauvegardes sont gardées 10 jours
- Les métriques sont conservées selon la configuration Prometheus

---

## Support

Pour toute question ou problème concernant les agents d'automatisation:

1. Consulter ce document
2. Vérifier les logs dans `logs/`
3. Consulter le dashboard Grafana
4. Contacter l'équipe DevOps

---

## Changelog

### v1.0.0 (2024-01-20)
- Création initiale des agents d'automatisation
- Pipeline CI/CD GitHub Actions
- Configuration Docker multi-conteneurs
- Monitoring Prometheus/Grafana
- Agent de migration Flyway
- Scripts de déploiement et maintenance
