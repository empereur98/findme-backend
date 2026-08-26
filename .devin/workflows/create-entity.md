---
description: Créer une nouvelle entité JPA avec repository et migration Flyway
---

# Workflow: Créer une nouvelle entité

Ce workflow guide la création d'une nouvelle entité JPA suivant la méthodologie du projet.

## Étapes

1. **Demander les informations de l'entité**
   - Nom de l'entité (ex: User, Product, Order)
   - Liste des champs avec leurs types et contraintes
   - Relations avec d'autres entités (si applicable)

2. **Créer la classe d'entité**
   - Créer le fichier dans `src/main/java/com/dhi/findme_backend/entity/`
   - Hériter de `BaseEntity` (et `Auditable` si nécessaire)
   - Ajouter les annotations JPA (@Entity, @Table, @Column, etc.)
   - Ajouter les annotations Lombok (@Getter, @Setter)
   - Ajouter les relations (@OneToMany, @ManyToOne, etc.)

3. **Créer l'interface repository**
   - Créer le fichier dans `src/main/java/com/dhi/findme_backend/repository/`
   - Étendre `JpaRepository<Entité, UUID>`
   - Ajouter les méthodes de recherche personnalisées si nécessaire
   - Ajouter les annotations @Query pour les requêtes complexes

4. **Créer la migration Flyway**
   - Créer le fichier dans `src/main/resources/db/migration/`
   - Nommer le fichier: `V{version}__create_{table_name}.sql`
   - Définir la table avec les colonnes correspondantes
   - Ajouter les indexes nécessaires
   - Ajouter les contraintes foreign key si applicable

5. **Créer les tests du repository** (si requêtes custom)
   - Créer le fichier de test dans `src/test/java/com/dhi/findme_backend/repository/`
   - Utiliser @DataJpaTest
   - Utiliser Testcontainers pour PostgreSQL
   - Tester les méthodes de recherche personnalisées

## Exemple d'utilisation

```
/create-entity
```

L'agent vous demandera les détails de l'entité et créera tous les fichiers nécessaires.
