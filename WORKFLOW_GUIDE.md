# FindMe Backend - Workflow Guide

This guide documents the structured workflow for developing REST API endpoints in the FindMe Backend project, following Spring Boot best practices and contract-first design.

## Table of Contents

1. [Phase 1: Conception](#phase-1-conception)
2. [Phase 2: Project Structure](#phase-2-project-structure)
3. [Phase 3: Development Workflow](#phase-3-development-workflow)
4. [Phase 4: Testing Strategy](#phase-4-testing-strategy)
5. [Phase 5: Quality Verification](#phase-5-quality-verification)
6. [Phase 6: CI/CD Pipeline](#phase-6-cicd-pipeline)
7. [Phase 7: Cross-Cutting Concerns](#phase-7-cross-cutting-concerns)

---

## Phase 1: Conception

### Contract-First Design

**Before writing any code:**

1. **Define the OpenAPI contract** in `src/main/resources/openapi/` or update the existing specification
2. **Validate the contract** with API consumers (frontend teams, third-party integrations)
3. **Include in the contract:**
   - All endpoints with HTTP methods
   - Request/response schemas
   - HTTP status codes for all scenarios
   - Authentication requirements
   - Rate limiting considerations

### Domain Modeling

**Identify business aggregates before entities:**

- **Entity**: JPA persistence model (database representation)
- **DTO**: API request/response objects (external contract)
- **Domain Model**: Business logic objects (if needed for complex logic)

**Key principle**: Keep these three layers separate - never expose entities directly via API.

---

## Phase 2: Project Structure

The project follows a **layered architecture** suitable for small-to-medium projects:

```
com.dhi.findme_backend
├── controller/        // REST entry points, no business logic
├── service/           // Business logic orchestration
│   └── impl/          // Service implementations
├── repository/        // Data access (Spring Data JPA)
├── dto/               // Request/response objects
├── mapper/            // MapStruct DTO <-> Entity mapping
├── entity/            // JPA entities (extend BaseEntity)
├── exception/         // Custom exceptions + global handler
├── config/            // Security, CORS, OpenAPI, etc.
└── validation/        // Custom validators
```

**For larger projects**, consider switching to **feature-based architecture**:

```
com.dhi.findme_backend
├── user/
│   ├── UserController.java
│   ├── UserService.java
│   ├── UserRepository.java
│   ├── dto/
│   └── mapper/
├── order/
│   └── ...
└── shared/            // Cross-cutting code
```

---

## Phase 3: Development Workflow

Follow this order for **each new endpoint/feature** (TDD or TDD-like approach):

### Step 1: Define/Validate OpenAPI Contract

```yaml
# Example: User creation endpoint
paths:
  /api/v1/users:
    post:
      summary: Create a new user
      operationId: createUser
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/UserCreateRequest'
      responses:
        '201':
          description: User created successfully
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/UserResponse'
        '400':
          $ref: '#/components/schemas/ErrorResponse'
        '422':
          $ref: '#/components/schemas/ErrorResponse'
```

### Step 2: Entity + Repository

**Create the JPA entity:**

```java
@Entity
@Table(name = "users")
@Getter
@Setter
public class User extends BaseEntity {
    
    @Column(nullable = false, unique = true, length = 100)
    private String username;
    
    @Column(nullable = false, length = 255)
    private String email;
    
    @Column(nullable = false)
    private String password;
    
    // Relationships, indexes, etc.
}
```

**Create the repository interface:**

```java
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    
    Optional<User> findByUsername(String username);
    
    Optional<User> findByEmail(String email);
    
    @Query("SELECT u FROM User u WHERE u.username LIKE %:keyword%")
    Page<User> searchByUsername(@Param("keyword") String keyword, Pageable pageable);
}
```

**Create Flyway migration:**

```sql
-- V1__create_users_table.sql
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
```

### Step 3: Repository Tests (if custom queries)

```java
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
class UserRepositoryTest {
    
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");
    
    @Autowired
    private UserRepository userRepository;
    
    @Test
    void findByUsername_shouldReturnUser() {
        // Given
        User user = new User();
        user.setUsername("testuser");
        userRepository.save(user);
        
        // When
        Optional<User> found = userRepository.findByUsername("testuser");
        
        // Then
        assertTrue(found.isPresent());
        assertEquals("testuser", found.get().getUsername());
    }
}
```

### Step 4: DTO + Mapper

**Create request DTOs with validation:**

```java
public record UserCreateRequest(
        @NotBlank(message = "Username is required")
        @Size(min = 3, max = 100, message = "Username must be between 3 and 100 characters")
        String username,
        
        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        String email,
        
        @NotBlank(message = "Password is required")
        @Size(min = 8, message = "Password must be at least 8 characters")
        String password
) {}
```

**Create response DTOs:**

```java
public record UserResponse(
        UUID id,
        String username,
        String email,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
```

**Create MapStruct mapper:**

```java
@Mapper(componentModel = "spring")
public interface UserMapper {
    
    User toEntity(UserCreateRequest request);
    
    UserResponse toResponse(User entity);
    
    @Mapping(target = "password", ignore = true)
    User toEntity(UserUpdateRequest request);
    
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(UserUpdateRequest request, @MappingTarget User entity);
}
```

### Step 5: Service Layer

**Define service interface:**

```java
public interface UserService {
    
    UserResponse createUser(UserCreateRequest request);
    
    UserResponse getUserById(UUID id);
    
    Page<UserResponse> getAllUsers(Pageable pageable);
    
    UserResponse updateUser(UUID id, UserUpdateRequest request);
    
    void deleteUser(UUID id);
}
```

**Implement service with business logic:**

```java
@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {
    
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public UserResponse createUser(UserCreateRequest request) {
        // Business validation
        if (userRepository.existsByUsername(request.username())) {
            throw new BusinessException("Username already exists");
        }
        
        if (userRepository.existsByEmail(request.email())) {
            throw new BusinessException("Email already exists");
        }
        
        // Map and persist
        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.password()));
        User savedUser = userRepository.save(user);
        
        return userMapper.toResponse(savedUser);
    }
    
    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        return userMapper.toResponse(user);
    }
    
    // ... other methods
}
```

### Step 6: Service Unit Tests (TDD)

```java
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private UserMapper userMapper;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @InjectMocks
    private UserServiceImpl userService;
    
    @Test
    void createUser_shouldReturnUserResponse() {
        // Given
        UserCreateRequest request = new UserCreateRequest("testuser", "test@example.com", "password123");
        User user = new User();
        UserResponse response = new UserResponse(UUID.randomUUID(), "testuser", "test@example.com", null, null);
        
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(userMapper.toEntity(request)).thenReturn(user);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(response);
        
        // When
        UserResponse result = userService.createUser(request);
        
        // Then
        assertNotNull(result);
        assertEquals("testuser", result.username());
        verify(userRepository).save(user);
    }
    
    @Test
    void createUser_whenUsernameExists_shouldThrowException() {
        // Given
        UserCreateRequest request = new UserCreateRequest("existing", "test@example.com", "password123");
        when(userRepository.existsByUsername("existing")).thenReturn(true);
        
        // When/Then
        assertThrows(BusinessException.class, () -> userService.createUser(request));
        verify(userRepository, never()).save(any());
    }
}
```

### Step 7: Controller Layer

```java
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User management endpoints")
public class UserController {
    
    private final UserService userService;
    
    @PostMapping
    @Operation(summary = "Create a new user", description = "Creates a new user with the provided data")
    @ApiResponse(responseCode = "201", description = "User created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input")
    @ApiResponse(responseCode = "422", description = "Business rule violation")
    public ResponseEntity<UserResponse> createUser(
            @Valid @RequestBody UserCreateRequest request) {
        
        UserResponse response = userService.createUser(request);
        URI location = URI.create("/api/v1/users/" + response.id());
        return ResponseEntity.created(location).body(response);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID", description = "Retrieves a user by their unique identifier")
    @ApiResponse(responseCode = "200", description = "User found")
    @ApiResponse(responseCode = "404", description = "User not found")
    public ResponseEntity<UserResponse> getUserById(@PathVariable UUID id) {
        UserResponse response = userService.getUserById(id);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    @Operation(summary = "Get all users", description = "Retrieves a paginated list of users")
    @ApiResponse(responseCode = "200", description = "Users retrieved successfully")
    public ResponseEntity<Page<UserResponse>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "username,asc") String[] sort) {
        
        Pageable pageable = PageRequest.of(page, size, getSort(sort));
        Page<UserResponse> users = userService.getAllUsers(pageable);
        return ResponseEntity.ok(users);
    }
    
    // ... other endpoints
}
```

### Step 8: Controller Integration Tests

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
class UserControllerIntegrationTest {
    
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Test
    void createUser_shouldReturn201() {
        // Given
        UserCreateRequest request = new UserCreateRequest("testuser", "test@example.com", "password123");
        
        // When
        ResponseEntity<UserResponse> response = restTemplate.postForEntity(
                "/api/v1/users", request, UserResponse.class);
        
        // Then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("testuser", response.getBody().username());
    }
    
    @Test
    void createUser_whenInvalidInput_shouldReturn400() {
        // Given
        UserCreateRequest request = new UserCreateRequest("", "invalid-email", "short");
        
        // When
        ResponseEntity<ErrorResponse> response = restTemplate.postForEntity(
                "/api/v1/users", request, ErrorResponse.class);
        
        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody().errors());
    }
}
```

### Step 9: Error Handling Verification

Ensure the `@RestControllerAdvice` properly handles all exception scenarios:

- **400 Bad Request**: Validation errors
- **401 Unauthorized**: Authentication failures
- **403 Forbidden**: Authorization failures
- **404 Not Found**: Resource not found
- **409 Conflict**: Duplicate resource
- **422 Unprocessable Entity**: Business rule violations
- **500 Internal Server Error**: Unexpected errors

### Step 10: Documentation

Add OpenAPI annotations to controller methods:

```java
@Operation(
    summary = "Create a new user",
    description = "Creates a new user account with the provided credentials"
)
@io.swagger.v3.oas.annotations.responses.ApiResponse(
    responseCode = "201",
    description = "User created successfully",
    content = @Content(
        schema = @Schema(implementation = UserResponse.class)
    )
)
@io.swagger.v3.oas.annotations.responses.ApiResponse(
    responseCode = "400",
    description = "Invalid input data",
    content = @Content(
        schema = @Schema(implementation = ErrorResponse.class)
    )
)
```

---

## Phase 4: Testing Strategy

### Test Pyramid

```
        /\
       /E2E\        <- Few: Full environment (Testcontainers)
      /------\
     /Integr. \     <- @SpringBootTest, real DB via Testcontainers
    /----------\
   / Unitaires  \   <- Majority: Services, mappers (Mockito, JUnit5)
  /--------------\
```

### Unit Tests

- **Scope**: Services, mappers, validators
- **Tools**: JUnit 5, Mockito
- **Speed**: Fast (< 1 second per test)
- **Isolation**: Mock all external dependencies

```java
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock private UserRepository userRepository;
    @InjectMocks private UserServiceImpl userService;
    // tests...
}
```

### Integration Tests

- **Scope**: Controllers, repositories
- **Tools**: `@SpringBootTest`, Testcontainers, MockMvc/WebTestClient
- **Speed**: Moderate (seconds per test)
- **Database**: Real PostgreSQL via Testcontainers

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class UserControllerIntegrationTest {
    @Container static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");
    // tests...
}
```

### Contract Tests (Optional but Recommended)

- **Scope**: API contract compliance
- **Tools**: Spring Cloud Contract or Pact
- **Purpose**: Ensure implementation matches OpenAPI spec

### Coverage Requirements

- **Minimum coverage**: 80%
- **JaCoCo plugin**: Integrated in Maven build
- **Build failure**: If coverage below threshold

---

## Phase 5: Quality Verification

### Static Analysis

- **Checkstyle**: Code style enforcement
- **SpotBugs**: Bug detection
- **SonarLint/SonarQube**: Code quality metrics

### Validation

- **Bean Validation**: Systematic on DTOs (`@NotNull`, `@Size`, custom validators)
- **Never validate on entities**: Only on DTOs
- **Custom validators**: For complex business rules

```java
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordValidator.class)
public @interface ValidPassword {
    String message() default "Password must meet security requirements";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
```

### Error Handling

- **Single `@RestControllerAdvice`**: Centralized error handling
- **RFC 7807 ProblemDetail**: Standardized error format
- **Consistent error responses**: Same structure across all endpoints

### Code Review

- **Pull Request mandatory**: No direct commits to main
- **CI blocks merge**: If tests or linting fail
- **Review checklist**: Follow project guidelines

---

## Phase 6: CI/CD Pipeline

### Pipeline Stages

```yaml
# Example GitHub Actions workflow
stages:
  - Build
  - Unit Tests
  - Integration Tests (Testcontainers)
  - Quality Analysis (SonarQube)
  - Build Docker Image
  - Deploy to Staging
  - Smoke Tests
  - Deploy to Production
```

### Quality Gates

- **Unit tests**: Must pass
- **Integration tests**: Must pass
- **Code coverage**: ≥ 80%
- **SonarQube Quality Gate**: Must pass
- **Security scan**: No critical vulnerabilities

---

## Phase 7: Cross-Cutting Concerns

### API Versioning

- **Version from day one**: `/api/v1/...`
- **Backward compatibility**: Don't break existing clients
- **Deprecation strategy**: Document and communicate changes

### Idempotence

- **PUT operations**: Must be idempotent
- **DELETE operations**: Must be idempotent
- **Proper HTTP codes**: 409 for conflicts, 422 for validation errors

### Pagination/Sorting/Filtering

- **Standardized approach**: Spring Data `Pageable` on all list endpoints
- **Default page size**: 20 (configurable)
- **Max page size**: 100 (prevent large payloads)
- **Sorting**: Configurable via query parameters

```java
@GetMapping
public ResponseEntity<Page<UserResponse>> getAllUsers(
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "20") int size,
    @RequestParam(defaultValue = "username,asc") String[] sort
) {
    Pageable pageable = PageRequest.of(page, size, getSort(sort));
    return ResponseEntity.ok(userService.getAllUsers(pageable));
}
```

### Security

- **Spring Security**: Configure from the start
- **JWT/OAuth2**: Authentication mechanism
- **Role-based access**: Authorization at method level
- **Never add security later**: Design it in from the beginning

```java
@PreAuthorize("hasRole('ADMIN')")
@DeleteMapping("/{id}")
public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
    userService.deleteUser(id);
    return ResponseEntity.noContent().build();
}
```

### Logging and Tracing

- **Structured logging**: JSON format preferred
- **MDC correlation-id**: Track requests across layers
- **Log levels**: Appropriate for each environment
- **Sensitive data**: Never log passwords, tokens, PII

```java
// Correlation ID is automatically added via CorrelationIdFilter
log.info("Processing user creation request for username: {}", request.username());
log.debug("User created successfully with ID: {}", user.getId());
```

---

## Quick Reference Checklist

For each new feature, ensure:

- [ ] OpenAPI contract defined and validated
- [ ] Entity extends `BaseEntity`
- [ ] Repository interface created
- [ ] Flyway migration added
- [ ] DTOs with Bean Validation
- [ ] MapStruct mapper created
- [ ] Service interface and implementation
- [ ] Service unit tests (Mockito)
- [ ] Controller with OpenAPI annotations
- [ ] Controller integration tests (Testcontainers)
- [ ] Error handling verified
- [ ] Documentation updated
- [ ] Code review completed
- [ ] CI/CD pipeline passes

---

## Additional Resources

- **Spring Boot Documentation**: https://spring.io/projects/spring-boot
- **Spring Data JPA**: https://spring.io/projects/spring-data-jpa
- **MapStruct**: https://mapstruct.org/
- **Springdoc OpenAPI**: https://springdoc.org/
- **Testcontainers**: https://testcontainers.com/
- **Bean Validation**: https://beanvalidation.org/
