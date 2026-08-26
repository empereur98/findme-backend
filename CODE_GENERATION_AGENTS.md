# Agents de Génération de Code - FindMe Backend

Ce document décrit les agents de génération de code qui automatisent le processus de développement en respectant strictement les workflows établis dans `WORKFLOW_GUIDE.md`.

## Table des matières

1. [Vue d'ensemble](#vue-densemble)
2. [Agent de Génération d'Entité](#agent-de-génération-dentité)
3. [Agent de Génération d'Endpoint](#agent-de-génération-dendpoint)
4. [Agent de Génération de Validateur](#agent-de-génération-de-validateur)
5. [Workflow Complet](#workflow-complet)
6. [Bonnes Pratiques](#bonnes-pratiques)

---

## Vue d'ensemble

Les agents de génération de code sont des classes Java qui génèrent automatiquement le code source en suivant les étapes définies dans les workflows de développement. Ils garantissent que:

- ✅ Le code respecte la structure du projet
- ✅ Les conventions de nommage sont respectées
- ✅ Les tests sont générés automatiquement
- ✅ La documentation OpenAPI est incluse
- ✅ Les meilleures pratiques Spring Boot sont appliquées

### Emplacement

Tous les agents se trouvent dans: `src/main/java/com/dhi/findme_backend/generator/`

---

## Agent de Génération d'Entité

**Classe**: `EntityGeneratorAgent.java`

### Description

Génère automatiquement une entité JPA complète avec son repository et la migration Flyway associée, en respectant le workflow de création d'entité.

### Workflow respecté

1. **Entité JPA** - Création de la classe d'entité avec annotations
2. **Repository** - Création de l'interface repository
3. **Migration Flyway** - Création du script SQL de migration

### Utilisation

```java
EntityGeneratorAgent agent = new EntityGeneratorAgent("User", "users")
    .addField("username", "String", false, true, 100)
    .addField("email", "String", false, true, 255)
    .addField("password", "String", false, false, 255)
    .addField("active", "Boolean", false, false, 0)
    .setAuditable(true)
    .setMigrationVersion(1);

agent.generate();
```

### Paramètres

- **entityName**: Nom de la classe d'entité (ex: "User")
- **tableName**: Nom de la table en base de données (ex: "users")
- **addField()**: Ajoute un champ à l'entité
  - `name`: Nom du champ
  - `type`: Type Java (String, Integer, Boolean, etc.)
  - `nullable`: Si le champ peut être null
  - `unique`: Si le champ doit être unique
  - `length`: Longueur maximale (pour String)
- **setAuditable()**: Active/désactive l'audit (créé/modifié par)
- **setMigrationVersion()**: Version de la migration Flyway

### Fichiers générés

1. `src/main/java/com/dhi/findme_backend/entity/User.java`
   - Classe d'entité avec annotations JPA
   - Hérite de BaseEntity (et Auditable si activé)
   - Annotations Lombok (@Getter, @Setter)

2. `src/main/java/com/dhi/findme_backend/repository/UserRepository.java`
   - Interface repository étendant JpaRepository
   - Méthodes de recherche automatiques pour les champs uniques
   - Méthode findAll(Pageable)

3. `src/main/resources/db/migration/V1__create_users.sql`
   - Script SQL de création de table
   - Indexes pour les champs uniques
   - Colonnes d'audit si activé

### Exemple de sortie

```java
@Entity
@Table(name = "users")
@Getter
@Setter
public class User extends Auditable {
    
    @Column(name = "username", nullable = false, unique = true, length = 100)
    private String username;
    
    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;
    
    @Column(name = "password", nullable = false)
    private String password;
    
    @Column(name = "active", nullable = false)
    private Boolean active;
}
```

---

## Agent de Génération d'Endpoint

**Classe**: `EndpointGeneratorAgent.java`

### Description

Génère automatiquement un endpoint REST complet avec DTOs, mapper, service, controller et tests, en respectant le workflow de création d'endpoint.

### Workflow respecté

1. **DTOs** - Création des DTOs de requête et réponse
2. **Mapper** - Création du MapStruct mapper
3. **Service Interface** - Création de l'interface service
4. **Service Implémentation** - Création de l'implémentation service
5. **Tests Service** - Création des tests unitaires du service
6. **Controller** - Création du controller avec annotations OpenAPI
7. **Tests Controller** - Création des tests d'intégration du controller

### Utilisation

```java
EndpointGeneratorAgent agent = new EndpointGeneratorAgent("User", "/api/v1/users")
    .addRequestField("username", "String", "@NotBlank @Size(min=3, max=100)")
    .addRequestField("email", "String", "@NotBlank @Email")
    .addRequestField("password", "String", "@NotBlank @Size(min=8)")
    .addResponseField("username", "String")
    .addResponseField("email", "String")
    .addOperation("POST", "/", "Create user")
    .addOperation("GET", "/{id}", "Get user by ID")
    .addOperation("GET", "/", "Get all users")
    .addOperation("PUT", "/{id}", "Update user")
    .addOperation("DELETE", "/{id}", "Delete user");

agent.generate();
```

### Paramètres

- **entityName**: Nom de l'entité existante (ex: "User")
- **endpointPath**: Chemin de l'endpoint (ex: "/api/v1/users")
- **addRequestField()**: Ajoute un champ au DTO de requête
  - `name`: Nom du champ
  - `type`: Type Java
  - `validation`: Annotations Bean Validation
- **addResponseField()**: Ajoute un champ au DTO de réponse
  - `name`: Nom du champ
  - `type`: Type Java
- **addOperation()**: Définit une opération HTTP
  - `method`: Méthode HTTP (GET, POST, PUT, DELETE)
  - `path`: Chemin relatif
  - `description`: Description pour OpenAPI

### Fichiers générés

1. `src/main/java/com/dhi/findme_backend/dto/UserCreateRequest.java`
   - Record avec validation Bean Validation
   - Champs de requête

2. `src/main/java/com/dhi/findme_backend/dto/UserResponse.java`
   - Record avec champs de réponse
   - Inclut id, createdAt, updatedAt

3. `src/main/java/com/dhi/findme_backend/mapper/UserMapper.java`
   - Interface MapStruct
   - Méthodes de conversion Entity <-> DTO
   - Configuration pour les mises à jour partielles

4. `src/main/java/com/dhi/findme_backend/service/UserService.java`
   - Interface service avec méthodes CRUD
   - Annotations Spring Transaction

5. `src/main/java/com/dhi/findme_backend/service/impl/UserServiceImpl.java`
   - Implémentation du service
   - Logique métier de base
   - Gestion des exceptions

6. `src/test/java/com/dhi/findme_backend/service/impl/UserServiceImplTest.java`
   - Tests unitaires avec Mockito
   - Tests pour toutes les méthodes
   - Couverture des cas d'erreur

7. `src/main/java/com/dhi/findme_backend/controller/UserController.java`
   - Controller REST avec annotations OpenAPI
   - Endpoints CRUD complets
   - Validation des DTOs

8. `src/test/java/com/dhi/findme_backend/controller/UserControllerTest.java`
   - Tests d'intégration avec MockMvc
   - Tests pour tous les endpoints
   - Vérification des codes HTTP

### Exemple de sortie

```java
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User management endpoints")
public class UserController {
    
    private final UserService service;
    
    @PostMapping
    @Operation(summary = "Create a new user", description = "Creates a new user account")
    public ResponseEntity<UserResponse> create(
            @Valid @RequestBody UserCreateRequest request) {
        UserResponse response = service.create(request);
        URI location = URI.create("/api/v1/users/" + response.id());
        return ResponseEntity.created(location).body(response);
    }
    
    // ... autres endpoints
}
```

---

## Agent de Génération de Validateur

**Classe**: `ValidatorGeneratorAgent.java`

### Description

Génère automatiquement un validateur Bean Validation personnalisé avec son annotation et ses tests.

### Workflow respecté

1. **Annotation** - Création de l'annotation de validation
2. **Validateur** - Création de l'implémentation du validateur
3. **Tests** - Création des tests du validateur

### Utilisation

```java
ValidatorGeneratorAgent agent = new ValidatorGeneratorAgent(
    "ValidPassword",
    "String",
    "Password must meet security requirements"
).setValidationLogic(
    "return value.length() >= 8 &&\n" +
    "       value.matches(\".*[A-Z].*\") &&\n" +
    "       value.matches(\".*[a-z].*\") &&\n" +
    "       value.matches(\".*[0-9].*\") &&\n" +
    "       value.matches(\".*[!@#$%^&*].*\");"
);

agent.generate();
```

### Paramètres

- **validatorName**: Nom de l'annotation (ex: "ValidPassword")
- **fieldType**: Type de champ à valider (ex: "String")
- **errorMessage**: Message d'erreur par défaut
- **setValidationLogic()**: Logique de validation personnalisée

### Fichiers générés

1. `src/main/java/com/dhi/findme_backend/validation/ValidPassword.java`
   - Annotation de validation
   - Configuration Bean Validation

2. `src/main/java/com/dhi/findme_backend/validation/ValidPasswordValidator.java`
   - Implémentation ConstraintValidator
   - Logique de validation

3. `src/test/java/com/dhi/findme_backend/validation/ValidPasswordValidatorTest.java`
   - Tests unitaires du validateur
   - Tests cas valides et invalides

### Exemple de sortie

```java
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidPasswordValidator.class)
public @interface ValidPassword {
    String message() default "Password must meet security requirements";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
```

---

## Workflow Complet

Pour créer une nouvelle fonctionnalité complète, suivez cet ordre:

### Étape 1: Créer l'entité

```java
EntityGeneratorAgent entityAgent = new EntityGeneratorAgent("Product", "products")
    .addField("name", "String", false, true, 200)
    .addField("description", "String", false, false, 1000)
    .addField("price", "Double", false, false, 0)
    .addField("stock", "Integer", false, false, 0)
    .setAuditable(true)
    .setMigrationVersion(1);

entityAgent.generate();
```

### Étape 2: Créer l'endpoint

```java
EndpointGeneratorAgent endpointAgent = new EndpointGeneratorAgent("Product", "/api/v1/products")
    .addRequestField("name", "String", "@NotBlank @Size(max=200)")
    .addRequestField("description", "String", "@NotBlank @Size(max=1000)")
    .addRequestField("price", "Double", "@NotNull @Positive")
    .addRequestField("stock", "Integer", "@NotNull @Min(0)")
    .addResponseField("name", "String")
    .addResponseField("description", "String")
    .addResponseField("price", "Double")
    .addResponseField("stock", "Integer")
    .addOperation("POST", "/", "Create product")
    .addOperation("GET", "/{id}", "Get product by ID")
    .addOperation("GET", "/", "Get all products")
    .addOperation("PUT", "/{id}", "Update product")
    .addOperation("DELETE", "/{id}", "Delete product");

endpointAgent.generate();
```

### Étape 3: (Optionnel) Créer des validateurs personnalisés

```java
ValidatorGeneratorAgent validatorAgent = new ValidatorGeneratorAgent(
    "ValidPrice",
    "Double",
    "Price must be positive and less than 10000"
).setValidationLogic(
    "return value > 0 && value < 10000;"
);

validatorAgent.generate();
```

### Étape 4: Exécuter les tests

```bash
./mvnw test
```

### Étape 5: Personnaliser le code généré

Les agents génèrent du code de base. Vous devez ensuite:

1. **Ajouter la logique métier** dans le service
2. **Personnaliser les requêtes** dans le repository si nécessaire
3. **Ajuster les validations** selon vos besoins
4. **Compléter la documentation** OpenAPI

---

## Bonnes Pratiques

### 1. Utiliser les agents comme point de départ

Les agents génèrent du code de base qui respecte les conventions. Utilisez ce code comme point de départ et personnalisez-le selon vos besoins.

### 2. Ne pas régénérer sur du code existant

Si vous avez déjà personnalisé du code, ne régénérez pas les mêmes fichiers. Les agents écraseront vos modifications.

### 3. Versionner le code généré

Le code généré doit être versionné comme tout autre code. Les agents sont là pour accélérer le développement initial, pas pour remplacer le développement manuel.

### 4. Compléter les tests

Les tests générés sont basiques. Ajoutez des tests spécifiques à votre logique métier pour assurer une couverture complète.

### 5. Respecter les conventions

Les agents respectent les conventions du projet. Continuez à suivre ces conventions lorsque vous modifiez le code généré.

### 6. Utiliser les agents pour les prototypes

Les agents sont particulièrement utiles pour:
- Créer rapidement des prototypes
- Démarrer de nouvelles fonctionnalités
- Maintenir la cohérence du code

### 7. Personnaliser les agents

Si vous avez besoin de fonctionnalités spécifiques, n'hésitez pas à modifier les agents pour qu'ils génèrent du code adapté à vos besoins.

---

## Intégration avec les Workflows

Les agents de génération de code sont conçus pour être utilisés en parallèle avec les workflows définis dans `.devin/workflows/`:

- `/create-entity` peut utiliser `EntityGeneratorAgent`
- `/create-endpoint` peut utiliser `EndpointGeneratorAgent`
- `/create-validator` peut utiliser `ValidatorGeneratorAgent`

Vous pouvez modifier ces workflows pour intégrer les agents de génération de code et automatiser davantage le processus.

---

## Limitations

### Ce que les agents NE font PAS

- ❌ Générer la logique métier complexe
- ❌ Créer des relations entre entités
- ❌ Générer des requêtes SQL complexes
- ❌ Créer des tests d'intégration avec Testcontainers
- ❌ Générer la documentation complète OpenAPI
- ❌ Gérer les conflits de fusion

### Ce que vous devez faire manuellement

- ✅ Implémenter la logique métier spécifique
- ✅ Ajouter les relations entre entités
- ✅ Créer des requêtes repository personnalisées
- ✅ Écrire des tests d'intégration complets
- ✅ Documenter les endpoints OpenAPI en détail
- ✅ Gérer les conflits lors des modifications

---

## Exemples Avancés

### Génération avec relations

```java
// D'abord créer les entités
EntityGeneratorAgent categoryAgent = new EntityGeneratorAgent("Category", "categories")
    .addField("name", "String", false, true, 100)
    .setAuditable(true);
categoryAgent.generate();

EntityGeneratorAgent productAgent = new EntityGeneratorAgent("Product", "products")
    .addField("name", "String", false, true, 200)
    .addField("categoryId", "UUID", false, false, 0)
    .setAuditable(true);
productAgent.generate();

// Puis ajouter manuellement la relation @ManyToOne dans Product.java
```

### Génération avec logique métier personnalisée

```java
// Générer l'endpoint de base
EndpointGeneratorAgent agent = new EndpointGeneratorAgent("Order", "/api/v1/orders")
    // ... configuration de base
agent.generate();

// Puis personnaliser OrderServiceImpl avec votre logique métier
// - Calcul des totaux
// - Validation des stocks
// - Gestion des statuts
// etc.
```

---

## Dépannage

### Erreur: "Le fichier existe déjà"

Si un fichier existe déjà, l'agent va l'écraser. Assurez-vous de vouloir régénérer ou sauvegardez vos modifications.

### Erreur: "Package introuvable"

Vérifiez que la structure des packages est correcte. Les agents créent les répertoires automatiquement.

### Erreur: "Compilation échouée"

Après génération, exécutez:
```bash
./mvnw clean compile
```

Les erreurs sont généralement dues à:
- Imports manquants
- Types incompatibles
- Classes d'entité inexistantes

---

## Support

Pour toute question ou problème concernant les agents de génération de code:

1. Consulter ce document
2. Vérifier `WORKFLOW_GUIDE.md` pour la méthodologie
3. Consulter les exemples dans les classes d'agents
4. Modifier les agents selon vos besoins spécifiques

---

## Changelog

### v1.0.0 (2024-01-20)
- Création initiale des agents de génération de code
- EntityGeneratorAgent pour la génération d'entités
- EndpointGeneratorAgent pour la génération d'endpoints
- ValidatorGeneratorAgent pour la génération de validateurs
- Documentation complète
