---
description: Configurer Spring Security avec JWT/OAuth2
---

# Workflow: Configuration de la sécurité

Ce workflow guide la configuration de Spring Security avec JWT pour l'authentification.

## Étapes

1. **Ajouter les dépendances de sécurité**
   - Vérifier que spring-boot-starter-security est dans pom.xml
   - Ajouter jjwt (JWT library) si nécessaire
   - Ajouter spring-security-oauth2-resource-server si OAuth2

2. **Créer la classe de configuration SecurityConfig**
   - Créer dans `src/main/java/com/dhi/findme_backend/config/`
   - Étendre WebSecurityConfigurerAdapter ou utiliser SecurityFilterChain
   - Configurer les endpoints publics (Swagger UI, actuator)
   - Configurer les endpoints sécurisés (/api/v1/**)
   - Configurer CORS
   - Configurer CSRF (désactivé pour API REST)

3. **Créer le JWT filter**
   - Créer dans `src/main/java/com/dhi/findme_backend/config/`
   - Étendre OncePerRequestFilter
   - Extraire le token du header Authorization
   - Valider le token
   - Définir l'authentication dans le SecurityContext

4. **Créer le JWT utility**
   - Créer dans `src/main/java/com/dhi/findme_backend/config/`
   - Méthodes pour générer un token
   - Méthodes pour valider un token
   - Méthodes pour extraire les claims

5. **Créer l'endpoint d'authentification**
   - Créer AuthController avec endpoint /api/v1/auth/login
   - Créer AuthService pour gérer l'authentification
   - Créer AuthRequest et AuthResponse DTOs
   - Utiliser AuthenticationManager de Spring Security

6. **Créer les tests de sécurité**
   - Tester l'accès sans token (401)
   - Tester l'accès avec token valide (200)
   - Tester l'accès avec token invalide (401)
   - Tester les rôles et permissions

7. **Mettre à jour OpenAPI**
   - Configurer le schéma de sécurité Bearer JWT
   - Ajouter @SecurityRequirement sur les endpoints sécurisés

## Exemple d'utilisation

```
/setup-security
```

L'agent configurera Spring Security avec JWT pour votre API.
