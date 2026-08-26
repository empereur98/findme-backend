# Documentation des Workflows Temporal

## Vue d'ensemble

L'orchestrateur de workflows Temporal est configuré pour orchestrer les processus métier complexes dans l'application findme-backend.

## Configuration

### Serveur Temporal
- **Adresse**: 127.0.0.1:7233
- **Namespace**: default
- **Task Queue**: ADDRESS_TASK_QUEUE

### Services Docker
- **PostgreSQL**: Port 5432
- **Temporal Server**: Ports 7233, 8233
- **Temporal UI**: Désactivé temporairement (problèmes de readiness)

## Workflows Disponibles

### AddressWorkflow

**Interface**: `com.dhi.findme_backend.temporal.workflow.AddressWorkflow`

**Méthodes**:
- `createAddress(AddressCreateRequest request, UUID userId)` - Crée une nouvelle adresse via workflow

**Implémentation**: `com.dhi.findme_backend.temporal.workflow.impl.AddressWorkflowImpl`

**Comportement**:
- Crée un stub d'activité dans le contexte du workflow
- Appelle l'activité `AddressActivity.createAddress()`
- Retourne le résultat de la création d'adresse

## Activités Disponibles

### AddressActivity

**Interface**: `com.dhi.findme_backend.temporal.activity.AddressActivity`

**Méthodes**:
- `createAddress(AddressCreateRequest request, UUID userId)` - Crée une nouvelle adresse
- `getAddressById(UUID addressId)` - Récupère une adresse par ID
- `deleteAddress(UUID addressId)` - Supprime une adresse
- `verifyAddress(UUID addressId)` - Vérifie une adresse

**Implémentation**: `com.dhi.findme_backend.temporal.activity.impl.AddressActivityImpl`

**Comportement**:
- Utilise le service `AddressService` existant
- Gère les exceptions métier
- Supporte les retries automatiques

## Endpoints API

### Démarrer un Workflow de Création d'Adresse

**Endpoint**: `POST /api/workflows/addresses/create`

**Paramètres**:
- `request` (body): AddressCreateRequest
- `userId` (query): UUID de l'utilisateur

**Sécurité**: Nécessite le rôle USER

**Réponse**: WorkflowExecution avec ID du workflow

**Exemple**:
```bash
curl -X POST http://localhost:8080/api/workflows/addresses/create \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Maison",
    "country": "Sénégal",
    "city": "Dakar",
    "district": "Plateau",
    "street": "Rue 123",
    "landmark": "Marché",
    "gpsLat": 14.7167,
    "gpsLng": -17.4677,
    "imageFacade": "https://example.com/facade.jpg",
    "type": "Personnel"
  }' \
  -G --data-urlencode "userId=<user-uuid>"
```

### Obtenir le Résultat d'un Workflow

**Endpoint**: `GET /api/workflows/addresses/{workflowId}/result`

**Paramètres**:
- `workflowId` (path): ID du workflow

**Sécurité**: Nécessite le rôle USER

**Réponse**: AddressResponse

### Obtenir le Statut d'un Workflow

**Endpoint**: `GET /api/workflows/addresses/{workflowId}/status`

**Paramètres**:
- `workflowId` (path): ID du workflow

**Sécurité**: Nécessite le rôle USER

**Réponse**: String (statut du workflow)

## Workers

### TemporalWorkerInitializer

**Classe**: `com.dhi.findme_backend.temporal.worker.TemporalWorkerInitializer`

**Comportement**:
- Démarrage automatique au démarrage de l'application
- Enregistre les activités et workflows
- Utilise la task queue "ADDRESS_TASK_QUEUE"
- Gère les erreurs de démarrage

## Tests

### AddressWorkflowTest

**Classe**: `com.dhi.findme_backend.temporal.workflow.AddressWorkflowTest`

**Tests**:
- Vérification de l'interface de workflow
- Vérification des méthodes de workflow

## Configuration Spring

### TemporalConfiguration

**Classe**: `com.dhi.findme_backend.config.TemporalConfiguration`

**Beans**:
- `workflowServiceStubs`: Connexion au serveur Temporal
- `workflowClient`: Client Temporal
- `workerFactory`: Factory pour créer des workers

## Utilisation

### Pour démarrer l'orchestrateur:

1. Démarrer les services Docker:
```bash
docker-compose up -d
```

2. Démarrer l'application Spring Boot:
```bash
./mvnw spring-boot:run
```

3. L'application se connectera automatiquement au serveur Temporal
4. Le worker démarrera automatiquement pour la task queue "ADDRESS_TASK_QUEUE"

### Pour utiliser les workflows:

1. Utiliser les endpoints API pour déclencher des workflows
2. Surveiller les workflows via les endpoints de statut
3. Récupérer les résultats via les endpoints de résultat

## Développement

### Ajouter un nouveau workflow:

1. Créer l'interface dans `src/main/java/com/dhi/findme_backend/temporal/workflow/`
2. Ajouter l'annotation `@WorkflowInterface`
3. Définir les méthodes avec `@WorkflowMethod`
4. Implémenter le workflow dans `src/main/java/com/dhi/findme_backend/temporal/workflow/impl/`
5. Enregistrer le workflow dans `TemporalWorkerInitializer`

### Ajouter une nouvelle activité:

1. Créer l'interface dans `src/main/java/com/dhi/findme_backend/temporal/activity/`
2. Ajouter l'annotation `@ActivityInterface`
3. Définir les méthodes avec `@ActivityMethod`
4. Implémenter l'activité dans `src/main/java/com/dhi/findme_backend/temporal/activity/impl/`
5. Enregistrer l'activité dans `TemporalWorkerInitializer`

## Monitoring

### Temporal UI

L'interface Temporal UI peut être activée en décommentant le service dans `compose.yaml` et en résolvant les problèmes de readiness.

### Logs

Les logs du worker sont disponibles dans les logs de l'application Spring Boot.

## Dépannage

### Problèmes de connexion Temporal

- Vérifier que le serveur Temporal est démarré: `docker-compose ps`
- Vérifier les logs du serveur Temporal: `docker-compose logs temporal`
- Vérifier la configuration dans `TemporalConfiguration`

### Problèmes de worker

- Vérifier que le worker est démarré dans les logs de l'application
- Vérifier que la task queue est correctement configurée
- Vérifier que les activités et workflows sont enregistrés

### Problèmes de readiness

- Le service Temporal UI est désactivé temporairement
- Pour l'activer, décommenter dans `compose.yaml` et résoudre les problèmes de healthcheck
