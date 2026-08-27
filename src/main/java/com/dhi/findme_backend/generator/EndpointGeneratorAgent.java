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
 * Agent de génération d'endpoint REST
 * Génère automatiquement DTOs, mapper, service, controller et tests
 * en respectant le workflow de développement établi
 */
public class EndpointGeneratorAgent {

    private static final String BASE_PACKAGE = "com.dhi.findme_backend";
    private static final String DTO_PACKAGE = BASE_PACKAGE + ".dto";
    private static final String MAPPER_PACKAGE = BASE_PACKAGE + ".mapper";
    private static final String SERVICE_PACKAGE = BASE_PACKAGE + ".service";
    private static final String SERVICE_IMPL_PACKAGE = BASE_PACKAGE + ".service.impl";
    private static final String VALIDATION_PACKAGE = BASE_PACKAGE + ".validation";
    private static final String CONTROLLER_PACKAGE = BASE_PACKAGE + ".controller";
    private static final String ENTITY_PACKAGE = BASE_PACKAGE + ".entity";
    private static final String REPOSITORY_PACKAGE = BASE_PACKAGE + ".repository";
    private static final String EXCEPTION_PACKAGE = BASE_PACKAGE + ".exception";

    private final String entityName;
    private final String endpointPath;
    private final List<FieldDefinition> requestFields;
    private final List<FieldDefinition> responseFields;
    private final List<OperationDefinition> operations;

    public EndpointGeneratorAgent(String entityName, String endpointPath) {
        this.entityName = entityName;
        this.endpointPath = endpointPath;
        this.requestFields = new ArrayList<>();
        this.responseFields = new ArrayList<>();
        this.operations = new ArrayList<>();
    }

    public EndpointGeneratorAgent addRequestField(String name, String type, String validation) {
        requestFields.add(new FieldDefinition(name, type, validation));
        return this;
    }

    public EndpointGeneratorAgent addResponseField(String name, String type) {
        responseFields.add(new FieldDefinition(name, type, null));
        return this;
    }

    public EndpointGeneratorAgent addOperation(String method, String path, String description) {
        operations.add(new OperationDefinition(method, path, description));
        return this;
    }

    /**
     * Génère tous les fichiers selon le workflow
     * Étape 1: DTOs de requête/réponse
     * Étape 2: MapStruct mapper
     * Étape 3: Interface service
     * Étape 4: Implémentation service
     * Étape 5: Tests unitaires service
     * Étape 6: Controller
     * Étape 7: Tests d'intégration controller
     */
    public void generate() throws IOException {
        System.out.println("🤖 Agent de génération d'endpoint démarré");
        System.out.println("📦 Entité: " + entityName);
        System.out.println("📍 Endpoint: " + endpointPath);
        System.out.println("");

        // Étape 1: Générer les DTOs
        generateRequestDTO();
        System.out.println("✅ DTO de requête généré");
        generateResponseDTO();
        System.out.println("✅ DTO de réponse généré");

        // Étape 2: Générer le mapper
        generateMapper();
        System.out.println("✅ MapStruct mapper généré");

        // Étape 3: Générer l'interface service
        generateServiceInterface();
        System.out.println("✅ Interface service générée");

        // Étape 4: Générer l'implémentation service
        generateServiceImplementation();
        System.out.println("✅ Implémentation service générée");

        // Étape 5: Générer les tests unitaires du service
        generateServiceTests();
        System.out.println("✅ Tests unitaires service générés");

        // Étape 6: Générer le controller
        generateController();
        System.out.println("✅ Controller généré");

        // Étape 7: Générer les tests d'intégration du controller
        generateControllerTests();
        System.out.println("✅ Tests d'intégration controller générés");

        System.out.println("");
        System.out.println("🎉 Génération terminée avec succès");
    }

    private void generateRequestDTO() throws IOException {
        String className = entityName + "CreateRequest";
        String fileName = className + ".java";
        Path path = Paths.get("src/main/java/com/dhi/findme_backend/dto", fileName);

        StringBuilder content = new StringBuilder();
        content.append("package ").append(DTO_PACKAGE).append(";\n\n");
        content.append("import jakarta.validation.constraints.*;\n");
        content.append("\n");
        content.append("public record ").append(className).append("(\n");

        for (int i = 0; i < requestFields.size(); i++) {
            FieldDefinition field = requestFields.get(i);
            if (field.validation != null && !field.validation.isEmpty()) {
                content.append("        ").append(field.validation).append("\n");
            }
            content.append("        ").append(field.type).append(" ").append(field.name);
            if (i < requestFields.size() - 1) {
                content.append(",\n");
            } else {
                content.append("\n");
            }
        }

        content.append(") {}\n");

        Files.createDirectories(path.getParent());
        try (FileWriter writer = new FileWriter(path.toFile())) {
            writer.write(content.toString());
        }
    }

    private void generateResponseDTO() throws IOException {
        String className = entityName + "Response";
        String fileName = className + ".java";
        Path path = Paths.get("src/main/java/com/dhi/findme_backend/dto", fileName);

        StringBuilder content = new StringBuilder();
        content.append("package ").append(DTO_PACKAGE).append(";\n\n");
        content.append("import java.time.LocalDateTime;\n");
        content.append("import java.util.UUID;\n");
        content.append("\n");
        content.append("public record ").append(className).append("(\n");
        content.append("        UUID id,\n");

        for (int i = 0; i < responseFields.size(); i++) {
            FieldDefinition field = responseFields.get(i);
            content.append("        ").append(field.type).append(" ").append(field.name);
            if (i < responseFields.size() - 1) {
                content.append(",\n");
            } else {
                content.append(",\n");
            }
        }

        content.append("        LocalDateTime createdAt,\n");
        content.append("        LocalDateTime updatedAt\n");
        content.append(") {}\n");

        Files.createDirectories(path.getParent());
        try (FileWriter writer = new FileWriter(path.toFile())) {
            writer.write(content.toString());
        }
    }

    private void generateMapper() throws IOException {
        String entityNameSimple = entityName;
        String mapperName = entityNameSimple + "Mapper";
        String fileName = mapperName + ".java";
        Path path = Paths.get("src/main/java/com/dhi/findme_backend/mapper", fileName);

        StringBuilder content = new StringBuilder();
        content.append("package ").append(MAPPER_PACKAGE).append(";\n\n");
        content.append("import ").append(ENTITY_PACKAGE).append(".").append(entityNameSimple).append(";\n");
        content.append("import ").append(DTO_PACKAGE).append(".").append(entityNameSimple).append("CreateRequest;\n");
        content.append("import ").append(DTO_PACKAGE).append(".").append(entityNameSimple).append("Response;\n");
        content.append("import org.mapstruct.Mapper;\n");
        content.append("import org.mapstruct.Mapping;\n");
        content.append("import org.mapstruct.MappingTarget;\n");
        content.append("import org.mapstruct.NullValuePropertyMappingStrategy;\n\n");
        content.append("@Mapper(componentModel = \"spring\")\n");
        content.append("public interface ").append(mapperName).append(" {\n\n");
        content.append("    ").append(entityNameSimple).append(" toEntity(")
                .append(entityNameSimple).append("CreateRequest request);\n\n");
        content.append("    ").append(entityNameSimple).append("Response toResponse(")
                .append(entityNameSimple).append(" entity);\n\n");
        content.append("    @Mapping(target = \"id\", ignore = true)\n");
        content.append("    @Mapping(target = \"createdAt\", ignore = true)\n");
        content.append("    @Mapping(target = \"updatedAt\", ignore = true)\n");
        content.append("    @Mapping(target = \"version\", ignore = true)\n");
        content.append("    ").append(entityNameSimple).append(" toEntityForUpdate(")
                .append(entityNameSimple).append("CreateRequest request);\n\n");
        content.append("    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)\n");
        content.append("    void updateEntityFromDto(").append(entityNameSimple)
                .append("CreateRequest request, @MappingTarget ").append(entityNameSimple).append(" entity);\n");
        content.append("}\n");

        Files.createDirectories(path.getParent());
        try (FileWriter writer = new FileWriter(path.toFile())) {
            writer.write(content.toString());
        }
    }

    private void generateServiceInterface() throws IOException {
        String entityNameSimple = entityName;
        String serviceName = entityNameSimple + "Service";
        String fileName = serviceName + ".java";
        Path path = Paths.get("src/main/java/com/dhi/findme_backend/service", fileName);

        StringBuilder content = new StringBuilder();
        content.append("package ").append(SERVICE_PACKAGE).append(";\n\n");
        content.append("import ").append(DTO_PACKAGE).append(".").append(entityNameSimple).append("CreateRequest;\n");
        content.append("import ").append(DTO_PACKAGE).append(".").append(entityNameSimple).append("Response;\n");
        content.append("import org.springframework.data.domain.Page;\n");
        content.append("import org.springframework.data.domain.Pageable;\n");
        content.append("import java.util.UUID;\n\n");
        content.append("public interface ").append(serviceName).append(" {\n\n");
        content.append("    ").append(entityNameSimple).append("Response create(")
                .append(entityNameSimple).append("CreateRequest request);\n\n");
        content.append("    ").append(entityNameSimple).append("Response getById(UUID id);\n\n");
        content.append("    Page<").append(entityNameSimple).append("Response> getAll(Pageable pageable);\n\n");
        content.append("    ").append(entityNameSimple).append("Response update(UUID id, ")
                .append(entityNameSimple).append("CreateRequest request);\n\n");
        content.append("    void delete(UUID id);\n");
        content.append("}\n");

        Files.createDirectories(path.getParent());
        try (FileWriter writer = new FileWriter(path.toFile())) {
            writer.write(content.toString());
        }
    }

    private void generateServiceImplementation() throws IOException {
        String entityNameSimple = entityName;
        String serviceName = entityNameSimple + "Service";
        String serviceImplName = serviceName + "Impl";
        String repositoryName = entityNameSimple + "Repository";
        String mapperName = entityNameSimple + "Mapper";
        String fileName = serviceImplName + ".java";
        Path path = Paths.get("src/main/java/com/dhi/findme_backend/service/impl", fileName);

        StringBuilder content = new StringBuilder();
        content.append("package ").append(SERVICE_IMPL_PACKAGE).append(";\n\n");
        content.append("import ").append(SERVICE_PACKAGE).append(".").append(serviceName).append(";\n");
        content.append("import ").append(REPOSITORY_PACKAGE).append(".").append(repositoryName).append(";\n");
        content.append("import ").append(MAPPER_PACKAGE).append(".").append(mapperName).append(";\n");
        content.append("import ").append(ENTITY_PACKAGE).append(".").append(entityNameSimple).append(";\n");
        content.append("import ").append(DTO_PACKAGE).append(".").append(entityNameSimple).append("CreateRequest;\n");
        content.append("import ").append(DTO_PACKAGE).append(".").append(entityNameSimple).append("Response;\n");
        content.append("import ").append(EXCEPTION_PACKAGE).append(".ResourceNotFoundException;\n");
        content.append("import ").append(EXCEPTION_PACKAGE).append(".BusinessException;\n");
        content.append("import org.springframework.data.domain.Page;\n");
        content.append("import org.springframework.data.domain.Pageable;\n");
        content.append("import org.springframework.stereotype.Service;\n");
        content.append("import org.springframework.transaction.annotation.Transactional;\n");
        content.append("import java.util.UUID;\n\n");
        content.append("@Service\n");
        content.append("@Transactional\n");
        content.append("public class ").append(serviceImplName).append(" implements ").append(serviceName).append(" {\n\n");
        content.append("    private final ").append(repositoryName).append(" repository;\n");
        content.append("    private final ").append(mapperName).append(" mapper;\n\n");
        content.append("    public ").append(serviceImplName).append("(").append(repositoryName).append(" repository, ")
                .append(mapperName).append(" mapper) {\n");
        content.append("        this.repository = repository;\n");
        content.append("        this.mapper = mapper;\n");
        content.append("    }\n\n");
        content.append("    @Override\n");
        content.append("    public ").append(entityNameSimple).append("Response create(")
                .append(entityNameSimple).append("CreateRequest request) {\n");
        content.append("        ").append(entityNameSimple).append(" entity = mapper.toEntity(request);\n");
        content.append("        ").append(entityNameSimple).append(" saved = repository.save(entity);\n");
        content.append("        return mapper.toResponse(saved);\n");
        content.append("    }\n\n");
        content.append("    @Override\n");
        content.append("    @Transactional(readOnly = true)\n");
        content.append("    public ").append(entityNameSimple).append("Response getById(UUID id) {\n");
        content.append("        ").append(entityNameSimple).append(" entity = repository.findById(id)\n");
        content.append("                .orElseThrow(() -> new ResourceNotFoundException(\"")
                .append(entityNameSimple).append("\", \"id\", id));\n");
        content.append("        return mapper.toResponse(entity);\n");
        content.append("    }\n\n");
        content.append("    @Override\n");
        content.append("    @Transactional(readOnly = true)\n");
        content.append("    public Page<").append(entityNameSimple).append("Response> getAll(Pageable pageable) {\n");
        content.append("        return repository.findAll(pageable).map(mapper::toResponse);\n");
        content.append("    }\n\n");
        content.append("    @Override\n");
        content.append("    public ").append(entityNameSimple).append("Response update(UUID id, ")
                .append(entityNameSimple).append("CreateRequest request) {\n");
        content.append("        ").append(entityNameSimple).append(" entity = repository.findById(id)\n");
        content.append("                .orElseThrow(() -> new ResourceNotFoundException(\"")
                .append(entityNameSimple).append("\", \"id\", id));\n");
        content.append("        mapper.updateEntityFromDto(request, entity);\n");
        content.append("        ").append(entityNameSimple).append(" updated = repository.save(entity);\n");
        content.append("        return mapper.toResponse(updated);\n");
        content.append("    }\n\n");
        content.append("    @Override\n");
        content.append("    public void delete(UUID id) {\n");
        content.append("        if (!repository.existsById(id)) {\n");
        content.append("            throw new ResourceNotFoundException(\"").append(entityNameSimple)
                .append("\", \"id\", id);\n");
        content.append("        }\n");
        content.append("        repository.deleteById(id);\n");
        content.append("    }\n");
        content.append("}\n");

        Files.createDirectories(path.getParent());
        try (FileWriter writer = new FileWriter(path.toFile())) {
            writer.write(content.toString());
        }
    }

    private void generateServiceTests() throws IOException {
        String entityNameSimple = entityName;
        String serviceName = entityNameSimple + "Service";
        String serviceImplName = serviceName + "Impl";
        String repositoryName = entityNameSimple + "Repository";
        String mapperName = entityNameSimple + "Mapper";
        String fileName = serviceImplName + "Test.java";
        Path path = Paths.get("src/test/java/com/dhi/findme_backend/service/impl", fileName);

        StringBuilder content = new StringBuilder();
        content.append("package com.dhi.findme_backend.service.impl;\n\n");
        content.append("import ").append(SERVICE_PACKAGE).append(".").append(serviceName).append(";\n");
        content.append("import ").append(REPOSITORY_PACKAGE).append(".").append(repositoryName).append(";\n");
        content.append("import ").append(MAPPER_PACKAGE).append(".").append(mapperName).append(";\n");
        content.append("import ").append(ENTITY_PACKAGE).append(".").append(entityNameSimple).append(";\n");
        content.append("import ").append(DTO_PACKAGE).append(".").append(entityNameSimple).append("CreateRequest;\n");
        content.append("import ").append(DTO_PACKAGE).append(".").append(entityNameSimple).append("Response;\n");
        content.append("import ").append(EXCEPTION_PACKAGE).append(".ResourceNotFoundException;\n");
        content.append("import org.junit.jupiter.api.Test;\n");
        content.append("import org.junit.jupiter.api.extension.ExtendWith;\n");
        content.append("import org.mockito.InjectMocks;\n");
        content.append("import org.mockito.Mock;\n");
        content.append("import org.mockito.junit.jupiter.MockitoExtension;\n");
        content.append("import org.springframework.data.domain.Page;\n");
        content.append("import org.springframework.data.domain.PageImpl;\n");
        content.append("import org.springframework.data.domain.PageRequest;\n");
        content.append("import org.springframework.data.domain.Pageable;\n");
        content.append("import java.util.Optional;\n");
        content.append("import java.util.UUID;\n");
        content.append("import static org.junit.jupiter.api.Assertions.*;\n");
        content.append("import static org.mockito.ArgumentMatchers.any;\n");
        content.append("import static org.mockito.Mockito.*;\n\n");
        content.append("@ExtendWith(MockitoExtension.class)\n");
        content.append("class ").append(serviceImplName).append("Test {\n\n");
        content.append("    @Mock\n");
        content.append("    private ").append(repositoryName).append(" repository;\n\n");
        content.append("    @Mock\n");
        content.append("    private ").append(mapperName).append(" mapper;\n\n");
        content.append("    @InjectMocks\n");
        content.append("    private ").append(serviceImplName).append(" service;\n\n");
        content.append("    @Test\n");
        content.append("    void create_shouldReturnResponse() {\n");
        content.append("        // Given\n");
        content.append("        ").append(entityNameSimple).append("CreateRequest request = new ")
                .append(entityNameSimple).append("CreateRequest();\n");
        content.append("        ").append(entityNameSimple).append(" entity = new ").append(entityNameSimple).append("();\n");
        content.append("        ").append(entityNameSimple).append("Response response = new ")
                .append(entityNameSimple).append("Response(UUID.randomUUID());\n");
        content.append("        when(mapper.toEntity(request)).thenReturn(entity);\n");
        content.append("        when(repository.save(entity)).thenReturn(entity);\n");
        content.append("        when(mapper.toResponse(entity)).thenReturn(response);\n\n");
        content.append("        // When\n");
        content.append("        ").append(entityNameSimple).append("Response result = service.create(request);\n\n");
        content.append("        // Then\n");
        content.append("        assertNotNull(result);\n");
        content.append("        verify(repository).save(entity);\n");
        content.append("    }\n\n");
        content.append("    @Test\n");
        content.append("    void getById_shouldReturnResponse() {\n");
        content.append("        // Given\n");
        content.append("        UUID id = UUID.randomUUID();\n");
        content.append("        ").append(entityNameSimple).append(" entity = new ").append(entityNameSimple).append("();\n");
        content.append("        ").append(entityNameSimple).append("Response response = new ")
                .append(entityNameSimple).append("Response(id);\n");
        content.append("        when(repository.findById(id)).thenReturn(Optional.of(entity));\n");
        content.append("        when(mapper.toResponse(entity)).thenReturn(response);\n\n");
        content.append("        // When\n");
        content.append("        ").append(entityNameSimple).append("Response result = service.getById(id);\n\n");
        content.append("        // Then\n");
        content.append("        assertNotNull(result);\n");
        content.append("        assertEquals(id, result.id());\n");
        content.append("    }\n\n");
        content.append("    @Test\n");
        content.append("    void getById_whenNotFound_shouldThrowException() {\n");
        content.append("        // Given\n");
        content.append("        UUID id = UUID.randomUUID();\n");
        content.append("        when(repository.findById(id)).thenReturn(Optional.empty());\n\n");
        content.append("        // When/Then\n");
        content.append("        assertThrows(ResourceNotFoundException.class, () -> service.getById(id));\n");
        content.append("    }\n\n");
        content.append("    @Test\n");
        content.append("    void getAll_shouldReturnPage() {\n");
        content.append("        // Given\n");
        content.append("        Pageable pageable = PageRequest.of(0, 20);\n");
        content.append("        Page<").append(entityNameSimple).append("> page = new PageImpl<>(List.of());\n");
        content.append("        when(repository.findAll(pageable)).thenReturn(page);\n\n");
        content.append("        // When\n");
        content.append("        Page<").append(entityNameSimple).append("Response> result = service.getAll(pageable);\n\n");
        content.append("        // Then\n");
        content.append("        assertNotNull(result);\n");
        content.append("        verify(repository).findAll(pageable);\n");
        content.append("    }\n\n");
        content.append("    @Test\n");
        content.append("    void delete_shouldCallRepository() {\n");
        content.append("        // Given\n");
        content.append("        UUID id = UUID.randomUUID();\n");
        content.append("        when(repository.existsById(id)).thenReturn(true);\n\n");
        content.append("        // When\n");
        content.append("        service.delete(id);\n\n");
        content.append("        // Then\n");
        content.append("        verify(repository).deleteById(id);\n");
        content.append("    }\n");
        content.append("}\n");

        Files.createDirectories(path.getParent());
        try (FileWriter writer = new FileWriter(path.toFile())) {
            writer.write(content.toString());
        }
    }

    private void generateController() throws IOException {
        String entityNameSimple = entityName;
        String controllerName = entityNameSimple + "Controller";
        String serviceName = entityNameSimple + "Service";
        String fileName = controllerName + ".java";
        Path path = Paths.get("src/main/java/com/dhi/findme_backend/controller", fileName);

        StringBuilder content = new StringBuilder();
        content.append("package ").append(CONTROLLER_PACKAGE).append(";\n\n");
        content.append("import ").append(SERVICE_PACKAGE).append(".").append(serviceName).append(";\n");
        content.append("import ").append(DTO_PACKAGE).append(".").append(entityNameSimple).append("CreateRequest;\n");
        content.append("import ").append(DTO_PACKAGE).append(".").append(entityNameSimple).append("Response;\n");
        content.append("import io.swagger.v3.oas.annotations.Operation;\n");
        content.append("import io.swagger.v3.oas.annotations.responses.ApiResponse;\n");
        content.append("import io.swagger.v3.oas.annotations.responses.ApiResponses;\n");
        content.append("import io.swagger.v3.oas.annotations.tags.Tag;\n");
        content.append("import jakarta.validation.Valid;\n");
        content.append("import org.springframework.data.domain.Page;\n");
        content.append("import org.springframework.data.domain.Pageable;\n");
        content.append("import org.springframework.http.HttpStatus;\n");
        content.append("import org.springframework.http.ResponseEntity;\n");
        content.append("import org.springframework.web.bind.annotation.*;\n");
        content.append("import java.net.URI;\n");
        content.append("import java.util.UUID;\n\n");
        content.append("@RestController\n");
        content.append("@RequestMapping(\"").append(endpointPath).append("\")\n");
        content.append("@Tag(name = \"").append(entityNameSimple).append("s\", description = \"")
                .append(entityNameSimple).append(" management endpoints\")\n");
        content.append("public class ").append(controllerName).append(" {\n\n");
        content.append("    private final ").append(serviceName).append(" service;\n\n");
        content.append("    public ").append(controllerName).append("(").append(serviceName).append(" service) {\n");
        content.append("        this.service = service;\n");
        content.append("    }\n\n");
        content.append("    @PostMapping\n");
        content.append("    @Operation(summary = \"Create a new ").append(entityNameSimple.toLowerCase())
                .append("\", description = \"Creates a new ").append(entityNameSimple.toLowerCase())
                .append(" with the provided data\")\n");
        content.append("    @ApiResponses(value = {\n");
        content.append("            @ApiResponse(responseCode = \"201\", description = \"")
                .append(entityNameSimple).append(" created successfully\"),\n");
        content.append("            @ApiResponse(responseCode = \"400\", description = \"Invalid input\"),\n");
        content.append("            @ApiResponse(responseCode = \"422\", description = \"Business rule violation\")\n");
        content.append("    })\n");
        content.append("    public ResponseEntity<").append(entityNameSimple).append("Response> create(\n");
        content.append("            @Valid @RequestBody ").append(entityNameSimple).append("CreateRequest request) {\n");
        content.append("        ").append(entityNameSimple).append("Response response = service.create(request);\n");
        content.append("        URI location = URI.create(\"").append(endpointPath).append("/\" + response.id());\n");
        content.append("        return ResponseEntity.created(location).body(response);\n");
        content.append("    }\n\n");
        content.append("    @GetMapping(\"/{id}\")\n");
        content.append("    @Operation(summary = \"Get ").append(entityNameSimple.toLowerCase())
                .append(" by ID\", description = \"Retrieves a ").append(entityNameSimple.toLowerCase())
                .append(" by its unique identifier\")\n");
        content.append("    @ApiResponses(value = {\n");
        content.append("            @ApiResponse(responseCode = \"200\", description = \"")
                .append(entityNameSimple).append(" found\"),\n");
        content.append("            @ApiResponse(responseCode = \"404\", description = \"")
                .append(entityNameSimple).append(" not found\")\n");
        content.append("    })\n");
        content.append("    public ResponseEntity<").append(entityNameSimple).append("Response> getById(@PathVariable UUID id) {\n");
        content.append("        ").append(entityNameSimple).append("Response response = service.getById(id);\n");
        content.append("        return ResponseEntity.ok(response);\n");
        content.append("    }\n\n");
        content.append("    @GetMapping\n");
        content.append("    @Operation(summary = \"Get all ").append(entityNameSimple.toLowerCase())
                .append("s\", description = \"Retrieves a paginated list of ")
                .append(entityNameSimple.toLowerCase()).append("s\")\n");
        content.append("    @ApiResponse(responseCode = \"200\", description = \"")
                .append(entityNameSimple).append("s retrieved successfully\")\n");
        content.append("    public ResponseEntity<Page<").append(entityNameSimple).append("Response>> getAll(\n");
        content.append("            Pageable pageable) {\n");
        content.append("        Page<").append(entityNameSimple).append("Response> page = service.getAll(pageable);\n");
        content.append("        return ResponseEntity.ok(page);\n");
        content.append("    }\n\n");
        content.append("    @PutMapping(\"/{id}\")\n");
        content.append("    @Operation(summary = \"Update ").append(entityNameSimple.toLowerCase())
                .append("\", description = \"Updates an existing ").append(entityNameSimple.toLowerCase())
                .append("\")\n");
        content.append("    @ApiResponses(value = {\n");
        content.append("            @ApiResponse(responseCode = \"200\", description = \"")
                .append(entityNameSimple).append(" updated successfully\"),\n");
        content.append("            @ApiResponse(responseCode = \"404\", description = \"")
                .append(entityNameSimple).append(" not found\")\n");
        content.append("    })\n");
        content.append("    public ResponseEntity<").append(entityNameSimple).append("Response> update(\n");
        content.append("            @PathVariable UUID id,\n");
        content.append("            @Valid @RequestBody ").append(entityNameSimple).append("CreateRequest request) {\n");
        content.append("        ").append(entityNameSimple).append("Response response = service.update(id, request);\n");
        content.append("        return ResponseEntity.ok(response);\n");
        content.append("    }\n\n");
        content.append("    @DeleteMapping(\"/{id}\")\n");
        content.append("    @Operation(summary = \"Delete ").append(entityNameSimple.toLowerCase())
                .append("\", description = \"Deletes a ").append(entityNameSimple.toLowerCase())
                .append(" by its ID\")\n");
        content.append("    @ApiResponses(value = {\n");
        content.append("            @ApiResponse(responseCode = \"204\", description = \"")
                .append(entityNameSimple).append(" deleted successfully\"),\n");
        content.append("            @ApiResponse(responseCode = \"404\", description = \"")
                .append(entityNameSimple).append(" not found\")\n");
        content.append("    })\n");
        content.append("    public ResponseEntity<Void> delete(@PathVariable UUID id) {\n");
        content.append("        service.delete(id);\n");
        content.append("        return ResponseEntity.noContent().build();\n");
        content.append("    }\n");
        content.append("}\n");

        Files.createDirectories(path.getParent());
        try (FileWriter writer = new FileWriter(path.toFile())) {
            writer.write(content.toString());
        }
    }

    private void generateControllerTests() throws IOException {
        String entityNameSimple = entityName;
        String controllerName = entityNameSimple + "Controller";
        String fileName = controllerName + "Test.java";
        Path path = Paths.get("src/test/java/com/dhi/findme_backend/controller", fileName);

        StringBuilder content = new StringBuilder();
        content.append("package com.dhi.findme_backend.controller;\n\n");
        content.append("import ").append(CONTROLLER_PACKAGE).append(".").append(controllerName).append(";\n");
        content.append("import ").append(DTO_PACKAGE).append(".").append(entityNameSimple).append("CreateRequest;\n");
        content.append("import ").append(DTO_PACKAGE).append(".").append(entityNameSimple).append("Response;\n");
        content.append("import ").append(SERVICE_PACKAGE).append(".").append(entityNameSimple).append("Service;\n");
        content.append("import com.fasterxml.jackson.databind.ObjectMapper;\n");
        content.append("import org.junit.jupiter.api.Test;\n");
        content.append("import org.springframework.beans.factory.annotation.Autowired;\n");
        content.append("import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;\n");
        content.append("import org.springframework.boot.test.mock.mockito.MockBean;\n");
        content.append("import org.springframework.data.domain.Page;\n");
        content.append("import org.springframework.data.domain.PageImpl;\n");
        content.append("import org.springframework.http.MediaType;\n");
        content.append("import org.springframework.test.web.servlet.MockMvc;\n");
        content.append("import java.util.UUID;\n");
        content.append("import java.util.List;\n");
        content.append("import static org.mockito.ArgumentMatchers.any;\n");
        content.append("import static org.mockito.Mockito.when;\n");
        content.append("import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;\n");
        content.append("import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;\n\n");
        content.append("@WebMvcTest(").append(controllerName).append(".class)\n");
        content.append("class ").append(controllerName).append("Test {\n\n");
        content.append("    @Autowired\n");
        content.append("    private MockMvc mockMvc;\n\n");
        content.append("    @Autowired\n");
        content.append("    private ObjectMapper objectMapper;\n\n");
        content.append("    @MockBean\n");
        content.append("    private ").append(entityNameSimple).append("Service service;\n\n");
        content.append("    @Test\n");
        content.append("    void create_shouldReturn201() throws Exception {\n");
        content.append("        // Given\n");
        content.append("        ").append(entityNameSimple).append("CreateRequest request = new ")
                .append(entityNameSimple).append("CreateRequest();\n");
        content.append("        ").append(entityNameSimple).append("Response response = new ")
                .append(entityNameSimple).append("Response(UUID.randomUUID());\n");
        content.append("        when(service.create(any())).thenReturn(response);\n\n");
        content.append("        // When/Then\n");
        content.append("        mockMvc.perform(post(\"").append(endpointPath).append("\")\n");
        content.append("                        .contentType(MediaType.APPLICATION_JSON)\n");
        content.append("                        .content(objectMapper.writeValueAsString(request)))\n");
        content.append("                .andExpect(status().isCreated());\n");
        content.append("    }\n\n");
        content.append("    @Test\n");
        content.append("    void getById_shouldReturn200() throws Exception {\n");
        content.append("        // Given\n");
        content.append("        UUID id = UUID.randomUUID();\n");
        content.append("        ").append(entityNameSimple).append("Response response = new ")
                .append(entityNameSimple).append("Response(id);\n");
        content.append("        when(service.getById(id)).thenReturn(response);\n\n");
        content.append("        // When/Then\n");
        content.append("        mockMvc.perform(get(\"").append(endpointPath).append("/\" + id))\n");
        content.append("                .andExpect(status().isOk());\n");
        content.append("    }\n\n");
        content.append("    @Test\n");
        content.append("    void getAll_shouldReturn200() throws Exception {\n");
        content.append("        // Given\n");
        content.append("        Page<").append(entityNameSimple).append("Response> page = new PageImpl<>(List.of());\n");
        content.append("        when(service.getAll(any())).thenReturn(page);\n\n");
        content.append("        // When/Then\n");
        content.append("        mockMvc.perform(get(\"").append(endpointPath).append("\"))\n");
        content.append("                .andExpect(status().isOk());\n");
        content.append("    }\n\n");
        content.append("    @Test\n");
        content.append("    void delete_shouldReturn204() throws Exception {\n");
        content.append("        // Given\n");
        content.append("        UUID id = UUID.randomUUID();\n\n");
        content.append("        // When/Then\n");
        content.append("        mockMvc.perform(delete(\"").append(endpointPath).append("/\" + id))\n");
        content.append("                .andExpect(status().isNoContent());\n");
        content.append("    }\n");
        content.append("}\n");

        Files.createDirectories(path.getParent());
        try (FileWriter writer = new FileWriter(path.toFile())) {
            writer.write(content.toString());
        }
    }

    public static void main(String[] args) throws IOException {
        // Exemple d'utilisation
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
    }

    private record FieldDefinition(String name, String type, String validation) {
    }

    private record OperationDefinition(String method, String path, String description) {
    }
}
