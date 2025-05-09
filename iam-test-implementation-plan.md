# IAM Module Test Implementation Plan

## Revised Approach

Based on the JaCoCo coverage report, we'll implement tests with the following key principles:

1. **Start with simpler components** to build momentum and establish testing patterns
2. **Achieve 80% coverage for each file** before moving to the next component
3. **Progress from simpler to more complex components** to establish testing patterns that can be reused

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
