---
description: Configurer l'application Spring Boot
---

# Workflow: Configuration de l'application

Ce workflow guide la configuration de l'application Spring Boot, y compris les propriétés, les profils, et les configurations de base.

## Étapes

1. **Configurer les propriétés de base**
   - Configurer `application.properties` ou `application.yml`
   - Définir le nom de l'application
   - Configurer le port du serveur
   - Configurer le contexte de l'application

2. **Configurer la base de données**
   - Configurer la connexion PostgreSQL
   - Configurer les pools de connexion (HikariCP)
   - Configurer JPA/Hibernate
   - Configurer Flyway pour les migrations

3. **Configurer les profils**
   - Créer des profils (dev, test, prod)
   - Configurer les propriétés spécifiques à chaque profil
   - Configurer les variables d'environnement

4. **Configurer la journalisation**
   - Configurer Logback ou Log4j2
   - Définir les niveaux de log
   - Configurer les appenders (console, fichier)
   - Configurer le format des logs

5. **Configurer OpenAPI/Swagger**
   - Configurer springdoc-openapi
   - Définir les métadonnées de l'API
   - Configurer les serveurs
   - Configurer les schémas de sécurité

6. **Configurer CORS**
   - Configurer les origines autorisées
   - Configurer les méthodes HTTP autorisées
   - Configurer les headers autorisés

7. **Configurer les tests**
   - Configurer les propriétés de test
   - Configurer Testcontainers
   - Configurer JaCoCo pour la couverture

## Exemple d'utilisation

```
/setup-config
```

L'agent configurera l'application Spring Boot avec les propriétés et configurations nécessaires.
