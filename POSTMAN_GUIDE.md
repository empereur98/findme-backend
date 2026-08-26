# Guide de Tests Postman - FindMe Backend

## Pourquoi tester avec Postman ?

### 1. **Validation fonctionnelle**
Les tests Postman permettent de vérifier que chaque endpoint répond correctement :
- Codes de statut HTTP appropriés (200, 201, 400, 401, 403, 404, etc.)
- Structure des réponses JSON conforme aux DTOs
- Validation des données d'entrée/sortie

### 2. **Tests d'intégration**
Tester l'interaction complète entre les composants :
- Authentification JWT → Accès aux endpoints protégés
- Base de données → Persistance des données
- Services → Logique métier
- Filtres de sécurité → Autorisations

### 3. **Débogage rapide**
Identifier les problèmes sans interface utilisateur :
- Erreurs de configuration (JWT, base de données)
- Problèmes de validation (Bean Validation)
- Exceptions non gérées
- Requêtes SQL incorrectes

### 4. **Documentation vivante**
La collection Postman sert de documentation exécutable :
- Chaque endpoint documenté avec description
- Exemples de requêtes/réponses
- Variables pour personnalisation
- Tests automatisés intégrés

### 5. **Tests de sécurité**
Vérifier la sécurité de l'application :
- Authentification JWT fonctionnelle
- Endpoints protégés inaccessibles sans token
- Rôles et autorisations respectés
- CORS configuré correctement

### 6. **Workflow de développement**
Faciliter le développement itératif :
- Tester rapidement les nouvelles fonctionnalités
- Valider les modifications sans redéploiement
- Isoler les problèmes de frontend vs backend
- Partager les tests avec l'équipe

---

## Configuration Préalable

### 1. Démarrer l'application
```bash
cd C:\Users\user\Desktop\projets\findme-backend
./mvnw.cmd spring-boot:run
```

L'application démarrera sur `http://localhost:8080`

### 2. Vérifier la base de données
Assurez-vous que PostgreSQL est démarré et que la base `findme_db` existe :
- Host: `localhost:5432`
- Database: `findme_db`
- User: `findme_user`
- Password: `findme_password`

### 3. Importer la collection Postman
1. Ouvrir Postman
2. Cliquer sur "Import" dans le coin supérieur gauche
3. Sélectionner le fichier `postman_collection.json`
4. La collection "FindMe Backend API" apparaîtra dans votre workspace

---

## Variables de Collection

La collection utilise des variables pour faciliter les tests :

| Variable | Description | Valeur par défaut |
|----------|-------------|-------------------|
| `baseUrl` | URL de base de l'API | `http://localhost:8080` |
| `token` | Token JWT d'authentification | Vide (auto-rempli après login) |
| `userId` | ID de l'utilisateur connecté | Vide (auto-rempli après login) |
| `notificationId` | ID d'une notification | À définir manuellement |

---

## Workflow de Test Recommandé

### Étape 1: Inscription d'un utilisateur
1. Ouvrir le dossier "Authentication"
2. Exécuter la requête **"Register"**
3. Vérifier que le statut est `201 Created`
4. La réponse contient le token JWT et les infos utilisateur

**Corps de la requête :**
```json
{
  "email": "testuser@example.com",
  "password": "Password123!",
  "firstName": "Test",
  "lastName": "User",
  "phone": "+33612345678"
}
```

### Étape 2: Connexion
1. Exécuter la requête **"Login"**
2. Le script de test automatiquement stockera :
   - Le token JWT dans la variable `token`
   - L'ID utilisateur dans la variable `userId`
3. Vérifier que le statut est `200 OK`

**Corps de la requête :**
```json
{
  "email": "testuser@example.com",
  "password": "Password123!"
}
```

### Étape 3: Tester les endpoints utilisateur
Avec le token stocké, vous pouvez maintenant tester les endpoints protégés :

1. **"Get Current User Profile"** - Récupérer le profil
2. **"Update User Profile"** - Modifier les informations
3. **"Change Password"** - Changer le mot de passe
4. **"Upload Avatar"** - Uploader une photo

### Étape 4: Tester les fonctionnalités avancées
- **Address** - Créer et gérer des adresses
- **Subscription** - Gérer l'abonnement
- **Support** - Créer des tickets de support
- **Payment** - Simuler des paiements
- **Notifications** - Gérer les notifications
- **Geo** - Géocodage d'adresses

### Étape 5: Tests Admin (optionnel)
Pour tester les endpoints admin, vous devez :
1. Créer un utilisateur avec le rôle `ADMIN` (via la base de données ou l'endpoint admin)
2. Se connecter avec ce compte admin
3. Exécuter les requêtes du dossier "Admin Users"

---

## Structure de la Collection

```
FindMe Backend API
├── Authentication
│   ├── Register
│   ├── Login (avec script de test auto)
│   ├── Logout
│   ├── Forgot Password
│   └── Reset Password
├── Users
│   ├── Get Current User Profile
│   ├── Update User Profile
│   ├── Change Password
│   └── Upload Avatar
├── Admin Users
│   ├── Get All Users
│   ├── Create User (Admin)
│   ├── Update User Plan
│   └── Delete User
├── Address
│   ├── Create Address
│   └── Get User Addresses
├── Subscription
│   ├── Get Current Subscription
│   └── Upgrade Subscription
├── Support
│   ├── Create Support Ticket
│   └── Get User Tickets
├── Payment
│   └── Initiate Payment
├── Notifications
│   ├── Get User Notifications
│   └── Mark as Read
└── Geo
    ├── Geocode Address
    └── Reverse Geocode
```

---

## Scripts de Test Automatisés

La requête "Login" inclut un script de test qui s'exécute automatiquement :

```javascript
var jsonData = pm.response.json();
pm.collectionVariables.set("token", jsonData.token);
pm.collectionVariables.set("userId", jsonData.user.id);
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});
pm.test("Token is present", function () {
    pm.expect(jsonData.token).to.exist;
});
```

Ce script :
- Extrait le token de la réponse
- Le stocke dans la variable de collection
- Vérifie que le statut est 200
- Vérifie que le token est présent

---

## Codes de Statut HTTP Attendus

| Code | Signification | Quand il apparaît |
|------|---------------|-------------------|
| 200 | OK | Requête réussie |
| 201 | Created | Ressource créée (register, create) |
| 204 | No Content | Suppression réussie |
| 400 | Bad Request | Erreur de validation |
| 401 | Unauthorized | Token manquant ou invalide |
| 403 | Forbidden | Permissions insuffisantes |
| 404 | Not Found | Ressource introuvable |
| 422 | Unprocessable Entity | Erreur de métier (email déjà utilisé) |
| 500 | Internal Server Error | Erreur serveur |

---

## Dépannage

### Problème: "Connection refused"
**Cause:** L'application n'est pas démarrée
**Solution:** Démarrer l'application avec `./mvnw.cmd spring-boot:run`

### Problème: "401 Unauthorized"
**Cause:** Token manquant ou expiré
**Solution:** Exécuter la requête "Login" pour rafraîchir le token

### Problème: "403 Forbidden"
**Cause:** Permissions insuffisantes (endpoint admin)
**Solution:** Se connecter avec un compte admin

### Problème: "422 Unprocessable Entity"
**Cause:** Email déjà utilisé ou validation échouée
**Solution:** Utiliser un email différent ou vérifier les champs

### Problème: "500 Internal Server Error"
**Cause:** Erreur serveur (voir logs)
**Solution:** Vérifier les logs de l'application pour identifier l'erreur

---

## Bonnes Pratiques

1. **Ordre des tests** : Toujours commencer par Register → Login
2. **Variables** : Utiliser les variables de collection au lieu de hardcoder
3. **Environnements** : Créer des environnements pour dev/staging/prod
4. **Tests automatisés** : Ajouter des scripts de test pour chaque requête
5. **Documentation** : Mettre à jour les descriptions des requêtes
6. **Nettoyage** : Supprimer les données de test après les tests

---

## Documentation API Complémentaire

L'API est également documentée via Swagger UI :
- URL: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

Cette documentation interactive permet de :
- Visualiser tous les endpoints
- Tester directement depuis le navigateur
- Voir les schémas de requêtes/réponses
- Télécharger la spécification OpenAPI

---

## Prochaines Étapes

Après avoir maîtrisé les tests Postman de base, vous pouvez :

1. **Créer des suites de tests** : Exécuter plusieurs requêtes en séquence
2. **Ajouter des assertions** : Vérifier le contenu des réponses
3. **Utiliser des environnements** : Gérer différentes configurations
4. **Intégrer avec CI/CD** : Automatiser les tests dans le pipeline
5. **Partager la collection** : Collaborer avec l'équipe

---

## Support

Pour toute question sur les tests Postman ou l'API FindMe Backend, consultez :
- La documentation Swagger UI
- Les fichiers de test dans `src/test/java`
- Le code source des contrôleurs dans `src/main/java/com/dhi/findme_backend/controller`
