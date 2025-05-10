# IAM Module Test Implementation Plan

## Implementation Status

We have successfully implemented tests for the first two phases of the plan. The current test coverage is as follows:

### Domain Layer
- **IamRole Enum**: 100% coverage
- **Domain Exceptions**: 100% coverage
- **Domain Events**: 100% coverage
- **User Entity**: 97% coverage (283 of 290 instructions)

### Application Use Cases
- **GetAllUsersUseCase**: 100% coverage
- **GetUserByCpfUseCase**: 100% coverage
- **DeleteUserUseCase**: 100% coverage
- **RegisterUserUseCase**: 100% coverage
- **AuthenticateUserUseCase**: 96% coverage (10 of 314 instructions missed)
- **UpdateUserUseCase**: 100% coverage

### Overall Coverage
- **Domain Layer**: 99% coverage
- **Application Layer**: 96% coverage

## Test Execution Notes

When running tests with `mvn clean test jacoco:report`, all tests are executed correctly, including those for the product and infrastructure modules. The JaCoCo report now shows much better coverage for these modules:

- Product domain entity: 100% coverage
- Product domain events: 100% coverage
- Product domain exceptions: 100% coverage
- Product infrastructure controllers: 99% coverage
- Product infrastructure messaging: 91-100% coverage
- Infrastructure messaging SQS model: 100% coverage

The overall project coverage is now at 63% (up from 18% in the previous report), which is a significant improvement.

## Test Quality Improvement Plan

To address the warnings and exceptions in test logs, we'll implement the following improvements:

### 1. Mockito Warning Resolution

For "unnecessary stubbing" warnings:
- Only stub methods that will actually be called in the test
- Use `lenient()` for stubs that may or may not be called depending on test path
- Refactor tests to avoid setting up unused mocks
- Add `@MockitoSettings(strictness = Strictness.LENIENT)` only when necessary

Example:
```java
// Instead of this:
when(userGateway.findByCpf(cpf)).thenReturn(Optional.empty());
when(userGateway.findByEmail(email)).thenReturn(Optional.empty()); // Unnecessary if test fails at first check

// Do this:
when(userGateway.findByCpf(cpf)).thenReturn(Optional.empty());
// Only add the second stub if the test will reach that point
```

### 2. Improved Exception Testing

For tests that expect exceptions:
- Use JUnit 5's assertThrows with specific exception types
- Verify exception message content when relevant
- Avoid catching exceptions in test methods unless necessary for verification
- Test both success and failure paths explicitly

Example:
```java
@Test
void shouldThrowExceptionWhenUserDoesNotExist() {
    // Given
    String cpf = "nonexistent";
    when(userGateway.findByCpf(cpf)).thenReturn(Optional.empty());
    
    // When/Then
    UserNotFoundException exception = assertThrows(
        UserNotFoundException.class,
        () -> getUserByCpfUseCase.getUserByCpf(cpf)
    );
    
    // Additional verification
    assertEquals("User not found with CPF: " + cpf, exception.getMessage());
}
```

### 3. Enhanced Test Structure

Improve test organization and readability:
- Follow the Arrange-Act-Assert (Given-When-Then) pattern consistently
- Use descriptive test method names that explain the scenario
- Group related tests using nested classes
- Use setup methods for common test fixtures

Example:
```java
@Nested
class WhenUserExists {
    @BeforeEach
    void setUp() {
        // Common setup for all tests in this context
    }
    
    @Test
    void shouldReturnUserWhenFoundByCpf() {
        // Test implementation
    }
}

@Nested
class WhenUserDoesNotExist {
    @BeforeEach
    void setUp() {
        // Different setup for this context
    }
    
    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        // Test implementation
    }
}
```

### 4. Improved Mock Verification

Enhance mock verification to ensure correct interactions:
- Verify exact number of method calls
- Use ArgumentCaptor to verify complex arguments
- Verify order of interactions when relevant
- Verify no unexpected interactions occurred

Example:
```java
@Test
void shouldPublishEventAfterUserCreation() {
    // Test setup and execution
    
    // Verification
    ArgumentCaptor<UserCreatedEvent> eventCaptor = ArgumentCaptor.forClass(UserCreatedEvent.class);
    verify(eventPublisher).publish(eventCaptor.capture());
    
    UserCreatedEvent capturedEvent = eventCaptor.getValue();
    assertEquals(userId, capturedEvent.getUserId());
    assertEquals(email, capturedEvent.getUserEmail());
}
```

### 5. Better Test Isolation

Ensure tests are properly isolated:
- Reset shared resources between tests
- Use @BeforeEach and @AfterEach for setup/teardown
- Consider using separate test profiles
- Avoid static state that persists between tests

Example:
```java
@ExtendWith(MockitoExtension.class)
class SomeTest {
    @Mock
    private UserGateway userGateway;
    
    private UserService userService;
    
    @BeforeEach
    void setUp() {
        userService = new UserService(userGateway);
        // Additional setup
    }
    
    @AfterEach
    void tearDown() {
        // Clean up any resources
    }
}
```

### 6. Improved Integration Testing

For tests involving external systems:
- Use TestContainers for database and AWS service testing
- Use WireMock for HTTP service mocking
- Create appropriate test profiles
- Implement proper cleanup after tests

Example:
```java
@Testcontainers
class MongoRepositoryTest {
    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:4.4.6");
    
    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }
    
    // Test methods
}
```

## Revised Approach

Based on the JaCoCo coverage report and the need for improved test quality, we'll implement tests with the following key principles:

1. **Start with simpler components** to build momentum and establish testing patterns
2. **Achieve 80% coverage for each file** before moving to the next component
3. **Progress from simpler to more complex components** to establish testing patterns that can be reused
4. **Focus on test quality** not just coverage metrics
5. **Improve testability** of the code through better design

## Implementation Order

### Phase 1: Domain Layer (Simplest Components)

#### Domain Exceptions (16 missed instructions)
- **Files to test:**
  - Various exception classes in `com.snackbar.iam.domain.exceptions`
- **Test approach:**
  - Simple tests for exception creation and message handling
  - Verify exception hierarchy and inheritance
- **Coverage target:** 80%+ for each file

#### Domain Events (93 missed instructions)
- **Files to test:**
  - Event classes in `com.snackbar.iam.domain.event`
- **Test approach:**
  - Test event creation with different parameters
  - Verify event properties and data integrity
- **Coverage target:** 80%+ for each file

#### Domain Core (15 missed instructions)
- **Files to test:**
  - Core domain interfaces/classes in `com.snackbar.iam.domain`
- **Test approach:**
  - Test interface implementations
  - Verify core domain behaviors
- **Coverage target:** 80%+ for each file

### Phase 2: Application Use Cases (Moderate Complexity)

#### GetAllUsersUseCase (10 missed instructions)
- **Test approach:**
  - Test successful retrieval of users
  - Test empty result handling
  - Test repository interaction
- **Coverage target:** 80%+ for this file

#### GetUserByCpfUseCase (21 missed instructions)
- **Test approach:**
  - Test successful user retrieval
  - Test behavior when user doesn't exist
  - Test exception handling
- **Coverage target:** 80%+ for this file

#### DeleteUserUseCase (38 missed instructions)
- **Test approach:**
  - Test successful user deletion
  - Test deletion of non-existent user
  - Test authorization checks
- **Coverage target:** 80%+ for this file

#### RegisterUserUseCase (65 missed instructions)
- **Test approach:**
  - Test successful user registration
  - Test duplicate user handling
  - Test validation of required fields
  - Test password encoding
- **Coverage target:** 80%+ for this file

#### AuthenticateUserUseCase (68 missed instructions)
- **Test approach:**
  - Test successful authentication
  - Test authentication with wrong password
  - Test authentication with non-existent user
  - Test token generation after successful authentication
- **Coverage target:** 80%+ for this file

#### UpdateUserUseCase (112 missed instructions)
- **Test approach:**
  - Test successful user update
  - Test validation logic
  - Test behavior when user doesn't exist
  - Test partial updates of user fields
- **Coverage target:** 80%+ for this file

### Phase 3: Infrastructure Components (Increasing Complexity)

#### DTOs and Mappers (155 missed instructions)
- **Files to test:**
  - Classes in `com.snackbar.iam.infrastructure.controllers.dto`
  - `UserDTOMapper` (48 missed instructions)
- **Test approach:**
  - Test mapping from domain entities to DTOs
  - Test mapping from DTOs to domain entities
  - Test validation annotations
- **Coverage target:** 80%+ for each file

#### Event Infrastructure (100 missed instructions)
- **Files to test:**
  - Classes in `com.snackbar.iam.infrastructure.event`
- **Test approach:**
  - Test event publishing
  - Test event handling
  - Test integration with domain events
- **Coverage target:** 80%+ for each file

#### Persistence Layer (190 missed instructions)
- **Files to test:**
  - Repository implementations in `com.snackbar.iam.infrastructure.persistence`
- **Test approach:**
  - Test CRUD operations
  - Test custom queries
  - Test entity mapping
  - Use in-memory database for testing
- **Coverage target:** 80%+ for each file

#### Gateway Components (137 missed instructions)
- **Files to test:**
  - Classes in `com.snackbar.iam.infrastructure.gateways`
- **Test approach:**
  - Test external service integrations
  - Test error handling in external calls
  - Use mocks for external dependencies
- **Coverage target:** 80%+ for each file

#### Configuration Classes (167 missed instructions)
- **Files to test:**
  - Classes in `com.snackbar.iam.infrastructure.config`
- **Test approach:**
  - Test bean creation
  - Test configuration properties
  - Test conditional configurations
- **Coverage target:** 80%+ for each file

### Phase 4: Controllers (Higher Complexity)

#### UserMgmtController (81 missed instructions)
- **Test approach:**
  - Test each endpoint with valid inputs
  - Test validation errors
  - Test error handling
  - Test HTTP status codes and response bodies
- **Coverage target:** 80%+ for this file

#### UserAuthController (97 missed instructions)
- **Test approach:**
  - Test authentication endpoint with valid credentials
  - Test authentication with invalid credentials
  - Test registration endpoint with valid data
  - Test registration with duplicate username/email
- **Coverage target:** 80%+ for this file

#### IamGlobalExceptionHandler (195 missed instructions)
- **Test approach:**
  - Test handling of each exception type
  - Verify correct HTTP status codes and response bodies
  - Test error message formatting
- **Coverage target:** 80%+ for this file

### Phase 5: Security Components (Most Complex)

#### Security Exceptions (43 missed instructions)
- **Files to test:**
  - Classes in `com.snackbar.iam.infrastructure.security.exception`
- **Test approach:**
  - Test exception creation and message handling
  - Test exception handling in security flows
- **Coverage target:** 80%+ for each file

#### IamUserDetailsService (23 missed instructions)
- **Test approach:**
  - Test successful user loading by username
  - Test behavior when user is not found
  - Test integration with user repository
- **Coverage target:** 80%+ for this file

#### UserDetailsAdapter (38 missed instructions)
- **Test approach:**
  - Test all UserDetails interface methods
  - Verify correct mapping from domain User to Spring Security UserDetails
- **Coverage target:** 80%+ for this file

#### JwtService (179 missed instructions)
- **Test approach:**
  - Test token generation with different claims
  - Test token validation (valid, expired, malformed)
  - Test extraction of username from token
  - Test extraction of claims from token
  - Test token expiration validation
- **Coverage target:** 80%+ for this file

#### IamJwtAuthenticationFilter (264 missed instructions)
- **Test approach:**
  - Test token extraction from request header
  - Test authentication flow with valid token
  - Test error handling with invalid/expired tokens
  - Test behavior when no token is provided
- **Coverage target:** 80%+ for this file

### Phase 6: Domain Entities (Complex Business Logic)

#### User Entity (290 missed instructions)
- **Test approach:**
  - Test entity creation with valid data
  - Test validation rules
  - Test business logic methods
  - Test equality and hashcode implementations
  - Test role-based functionality
- **Coverage target:** 80%+ for this file

## Testing Tools and Setup

1. **Testing Framework:**
   - JUnit 5 for test execution
   - Mockito for mocking dependencies
   - AssertJ for fluent assertions

2. **Spring Testing Support:**
   - `@SpringBootTest` for integration tests
   - `@WebMvcTest` for controller tests
   - `@DataJpaTest` for repository tests

3. **Security Testing:**
   - `spring-security-test` for authentication testing
   - JWT test utilities for token generation/validation

4. **Test Data Management:**
   - Test fixtures for common test data
   - Test data builders for complex objects

5. **Integration Testing:**
   - TestContainers for database and external services
   - WireMock for HTTP service mocking

## Continuous Coverage Monitoring

1. **JaCoCo Integration:**
   - Run JaCoCo after each test implementation phase
   - Generate reports to track progress
   - Ensure each file reaches 80% coverage before moving on

2. **Coverage Verification:**
   - Configure JaCoCo to fail the build if coverage drops below thresholds
   - Set up coverage gates in CI/CD pipeline

## Best Practices

1. **Test Naming Convention:**
   - Use descriptive test names that explain the scenario being tested
   - Follow the pattern: `should[ExpectedBehavior]When[StateUnderTest]`

2. **Test Organization:**
   - Group tests by functionality
   - Use nested classes for related test scenarios

3. **Test Independence:**
   - Ensure tests can run in any order
   - Clean up test data after each test

4. **Test Coverage Quality:**
   - Focus on testing behavior, not implementation details
   - Include both positive and negative test cases
   - Test edge cases and boundary conditions

5. **Documentation:**
   - Document complex test setups
   - Explain the purpose of test fixtures and utilities

## Special Considerations

1. **Security Testing:**
   - Ensure proper testing of authentication flows
   - Test authorization rules and role-based access
   - Verify token generation, validation, and expiration
   - Test security headers and CSRF protection
   - Validate secure password handling

2. **Error Handling:**
   - Test all exception paths thoroughly
   - Verify appropriate error responses
   - Test error logging and monitoring
   - Ensure sensitive information is not leaked in error messages

3. **Clean Architecture Boundaries:**
   - Ensure tests respect the clean architecture boundaries
   - Test adapters at the boundaries between layers
   - Verify that dependencies flow inward
   - Test that domain rules are not violated

4. **Performance Considerations:**
   - Include performance tests for token generation/validation
   - Test caching mechanisms
   - Verify database query performance
   - Test concurrent authentication requests

5. **Integration Points:**
   - Test integration with external systems
   - Verify correct behavior when external systems fail
   - Test retry mechanisms and circuit breakers
   - Validate data consistency across system boundaries
