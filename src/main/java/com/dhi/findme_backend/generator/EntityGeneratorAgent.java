package com.dhi.findme_backend.generator;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Agent de génération d'entité JPA
 * Génère automatiquement l'entité, le repository et la migration Flyway
 * en respectant le workflow de développement établi
 */
public class EntityGeneratorAgent {

    private static final String BASE_PACKAGE = "com.dhi.findme_backend";
    private static final String ENTITY_PACKAGE = BASE_PACKAGE + ".entity";
    private static final String REPOSITORY_PACKAGE = BASE_PACKAGE + ".repository";
    private static final String MIGRATION_PATH = "src/main/resources/db/migration";

    private final String entityName;
    private final String tableName;
    private final List<FieldDefinition> fields;
    private boolean auditable;
    private int migrationVersion;

    public EntityGeneratorAgent(String entityName, String tableName) {
        this.entityName = entityName;
        this.tableName = tableName;
        this.fields = new ArrayList<>();
        this.auditable = true;
        this.migrationVersion = 1;
    }

    public EntityGeneratorAgent addField(String name, String type, boolean nullable, boolean unique, int length) {
        fields.add(new FieldDefinition(name, type, nullable, unique, length));
        return this;
    }

    public EntityGeneratorAgent setAuditable(boolean auditable) {
        this.auditable = auditable;
        return this;
    }

    public EntityGeneratorAgent setMigrationVersion(int version) {
        this.migrationVersion = version;
        return this;
    }

    /**
     * Génère tous les fichiers selon le workflow
     * Étape 1: Entité JPA
     * Étape 2: Repository
     * Étape 3: Migration Flyway
     */
    public void generate() throws IOException {
        System.out.println("🤖 Agent de génération d'entité démarré");
        System.out.println("📦 Entité: " + entityName);
        System.out.println("📋 Table: " + tableName);
        System.out.println("");

        // Étape 1: Générer l'entité
        generateEntity();
        System.out.println("✅ Entité générée");

        // Étape 2: Générer le repository
        generateRepository();
        System.out.println("✅ Repository généré");

        // Étape 3: Générer la migration Flyway
        generateMigration();
        System.out.println("✅ Migration Flyway générée");

        System.out.println("");
        System.out.println("🎉 Génération terminée avec succès");
    }

    private void generateEntity() throws IOException {
        String className = entityName;
        String fileName = className + ".java";
        Path path = Paths.get("src/main/java/com/dhi/findme_backend/entity", fileName);

        StringBuilder content = new StringBuilder();
        content.append("package ").append(ENTITY_PACKAGE).append(";\n\n");
        content.append("import jakarta.persistence.*;\n");
        if (auditable) {
            content.append("import ").append(ENTITY_PACKAGE).append(".Auditable;\n");
        }
        content.append("\n");
        content.append("@Entity\n");
        content.append("@Table(name = \"").append(tableName).append("\")\n");
        content.append("public class ").append(className);
        if (auditable) {
            content.append(" extends Auditable");
        }
        content.append(" {\n\n");

        // Champs
        for (FieldDefinition field : fields) {
            content.append("    @Column(name = \"").append(field.name).append("\"");
            if (!field.nullable) {
                content.append(", nullable = false");
            }
            if (field.unique) {
                content.append(", unique = true");
            }
            if (field.length > 0) {
                content.append(", length = ").append(field.length);
            }
            content.append(")\n");
            content.append("    private ").append(field.type).append(" ").append(field.name).append(";\n\n");
        }

        content.append("}\n");

        Files.createDirectories(path.getParent());
        try (FileWriter writer = new FileWriter(path.toFile())) {
            writer.write(content.toString());
        }
    }

    private void generateRepository() throws IOException {
        String className = entityName;
        String repositoryName = className + "Repository";
        String fileName = repositoryName + ".java";
        Path path = Paths.get("src/main/java/com/dhi/findme_backend/repository", fileName);

        StringBuilder content = new StringBuilder();
        content.append("package ").append(REPOSITORY_PACKAGE).append(";\n\n");
        content.append("import ").append(ENTITY_PACKAGE).append(".").append(className).append(";\n");
        content.append("import org.springframework.data.jpa.repository.JpaRepository;\n");
        content.append("import org.springframework.stereotype.Repository;\n");
        content.append("import org.springframework.data.domain.Page;\n");
        content.append("import org.springframework.data.domain.Pageable;\n");
        content.append("import java.util.Optional;\n");
        content.append("import java.util.UUID;\n\n");
        content.append("@Repository\n");
        content.append("public interface ").append(repositoryName).append(" extends JpaRepository<")
                .append(className).append(", UUID> {\n\n");

        // Méthodes de recherche de base
        for (FieldDefinition field : fields) {
            if (field.unique) {
                content.append("    Optional<").append(className).append("> findBy")
                        .append(capitalize(field.name)).append("(").append(field.type).append(" ")
                        .append(field.name).append(");\n\n");
            }
        }

        // Méthode de recherche paginée
        content.append("    Page<").append(className).append("> findAll(Pageable pageable);\n");
        content.append("}\n");

        Files.createDirectories(path.getParent());
        try (FileWriter writer = new FileWriter(path.toFile())) {
            writer.write(content.toString());
        }
    }

    private void generateMigration() throws IOException {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String fileName = "V" + migrationVersion + "__create_" + tableName + ".sql";
        Path path = Paths.get(MIGRATION_PATH, fileName);

        StringBuilder content = new StringBuilder();
        content.append("-- Migration pour la table ").append(tableName).append("\n");
        content.append("-- Généré automatiquement par EntityGeneratorAgent\n");
        content.append("-- Date: ").append(LocalDateTime.now()).append("\n\n");

        content.append("CREATE TABLE ").append(tableName).append(" (\n");
        content.append("    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),\n");

        // Champs
        for (FieldDefinition field : fields) {
            content.append("    ").append(field.name).append(" ").append(getSqlType(field.type));
            if (!field.nullable) {
                content.append(" NOT NULL");
            }
            if (field.unique) {
                content.append(" UNIQUE");
            }
            content.append(",\n");
        }

        if (auditable) {
            content.append("    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,\n");
            content.append("    updated_at TIMESTAMP,\n");
            content.append("    created_by VARCHAR(100),\n");
            content.append("    updated_by VARCHAR(100),\n");
        }

        content.append("    version BIGINT NOT NULL DEFAULT 0\n");
        content.append(");\n\n");

        // Indexes
        for (FieldDefinition field : fields) {
            if (field.unique || field.length > 0) {
                content.append("CREATE INDEX idx_").append(tableName).append("_").append(field.name)
                        .append(" ON ").append(tableName).append("(").append(field.name).append(");\n");
            }
        }

        Files.createDirectories(path.getParent());
        try (FileWriter writer = new FileWriter(path.toFile())) {
            writer.write(content.toString());
        }
    }

    private String getSqlType(String javaType) {
        return switch (javaType) {
            case "String" -> "VARCHAR";
            case "Integer", "int" -> "INTEGER";
            case "Long", "long" -> "BIGINT";
            case "Double", "double" -> "DOUBLE PRECISION";
            case "Boolean", "boolean" -> "BOOLEAN";
            case "LocalDateTime" -> "TIMESTAMP";
            case "UUID" -> "UUID";
            default -> "TEXT";
        };
    }

    private String capitalize(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    public static void main(String[] args) throws IOException {
        // Exemple d'utilisation
        EntityGeneratorAgent agent = new EntityGeneratorAgent("User", "users")
                .addField("username", "String", false, true, 100)
                .addField("email", "String", false, true, 255)
                .addField("password", "String", false, false, 255)
                .addField("active", "Boolean", false, false, 0)
                .setAuditable(true)
                .setMigrationVersion(1);

        agent.generate();
    }

    private record FieldDefinition(String name, String type, boolean nullable, boolean unique, int length) {
    }
}
