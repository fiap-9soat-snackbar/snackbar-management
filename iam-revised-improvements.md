# IAM Module Revised Improvements

This document consolidates the analysis, implementation status, and improvement plan for the IAM module in the Snackbar Management application, incorporating clean architecture principles based on the product module implementation.

## Current Status Assessment

### API Endpoints ✅
All endpoints have been implemented:
- POST /api/user/auth/signup ✅
- POST /api/user/auth/login ✅
- GET /api/user/ ✅
- GET /api/user/cpf/{cpf} ✅
- DELETE /api/user/{id} ✅

### Error Handling ⚠️
- **Current Coverage**: ~50%
  - Missing proper exception handling for authentication failures
  - Missing domain-specific exceptions
- **Expected Coverage**: 100%
  - Need to create domain-specific exceptions:
    - `UserNotFoundException`
    - `InvalidCredentialsException`
    - `DuplicateUserException`
    - `InvalidUserDataException`
  - Need to implement proper error responses with appropriate HTTP status codes

### Field Validation ⚠️
- **Current Coverage**: ~30%
  - Basic validation exists but is inconsistent
  - Missing validation for:
    - CPF format validation
    - Email format validation
    - Password strength requirements
    - Role validation
- **Expected Coverage**: 100%
  - Implement comprehensive validation for all user fields
  - Add validation annotations to DTOs
  - Implement validation logic in domain entities

## Clean Architecture Analysis

The current IAM module has several architectural issues when compared to the product module's clean architecture implementation:

### Domain Layer Issues ⚠️

#### Entity Duplication and Inconsistency ❌
- **Issue**: Two nearly identical entities (`UserEntity` and `UserDetailsEntity`) represent the same domain concept.
- **Status**: Not implemented
- **Solution**: Consolidate into a single entity that implements `UserDetails` interface.

```java
// Example structure
package com.snackbar.iam.domain.entity;

public class User {
    private String id;
    private String name;
    private String email;
    private String cpf;
    private IamRole role;
    private String password;
    
    // Constructor with validation
    public User(String id, String name, String email, String cpf, IamRole role, String password) {
        validateUser(name, email, cpf, role, password);
        this.id = id;
        this.name = name;
        this.email = email;
        this.cpf = cpf;
        this.role = role;
        this.password = password;
    }
    
    // Business rules for user validation
    private static void validateUser(String name, String email, String cpf, IamRole role, String password) {
        // Validation logic here
    }
}
```

#### Field Name Inconsistency ❌
- **Issue**: In `UserEntity`, the field is named `name` but in the API documentation and DTOs it's referred to as `fullName`.
- **Status**: Not implemented
- **Solution**: Standardize naming across all layers.

#### Missing Domain Events ❌
- **Issue**: No domain events for important state changes
- **Status**: Not implemented
- **Solution**: Create domain events for user lifecycle:
  - `UserCreatedEvent`
  - `UserUpdatedEvent`
  - `UserDeletedEvent`

### Application Layer Issues ⚠️

#### Missing Use Case Interfaces ❌
- **Issue**: No clear separation between use case definition and implementation
- **Status**: Not implemented
- **Solution**: Create input ports (interfaces) for all use cases:
  - `RegisterUserInputPort`
  - `AuthenticateUserInputPort`
  - `GetUserByCpfInputPort`
  - `GetAllUsersInputPort`
  - `DeleteUserInputPort`

```java
// Example input port
package com.snackbar.iam.application.ports.in;

import com.snackbar.iam.domain.entity.User;

public interface RegisterUserInputPort {
    User registerUser(User user);
}
```

#### Repository Type Mismatch ❌
- **Issue**: `IamRepository` is defined for `UserEntity` but returns `UserDetailsEntity`.
- **Status**: Not implemented
- **Solution**: Create a proper gateway interface and implementation:

```java
// Example gateway interface
package com.snackbar.iam.application.gateways;

import com.snackbar.iam.domain.entity.User;
import java.util.List;
import java.util.Optional;

public interface UserGateway {
    User createUser(User user);
    Optional<User> findByCpf(String cpf);
    Optional<User> findByEmail(String email);
    List<User> findAll();
    void deleteById(String id);
}
```

#### Redundant Repositories ❌
- **Issue**: Both `IamRepository` and `UserRepository` serve identical purposes.
- **Status**: Not implemented
- **Solution**: Consolidate into a single repository interface.

#### Inconsistent Return Types ❌
- **Issue**: `AuthenticationService.authenticate()` returns `UserDetailsEntity` but `signup()` returns `UserEntity`.
- **Status**: Not implemented
- **Solution**: Standardize return types across service methods.

#### Missing Error Handling ❌
- **Issue**: `UserService.getUserByCpf()` uses `orElseThrow()` without specifying an exception.
- **Status**: Not implemented
- **Solution**: Provide a specific exception:

```java
.orElseThrow(() -> new UserNotFoundException("User not found with CPF: " + cpf));
```

#### Dependency on External Service ❌
- **Issue**: `AuthenticationController` depends on `OrderService` which is outside the IAM module.
- **Status**: Not implemented
- **Solution**: Remove this dependency or use an interface/port to maintain loose coupling.

#### Redundant Service Implementations ❌
- **Issue**: `IamService` and `IamServiceImpl` exist but are barely used, while most functionality is in `AuthenticationService`.
- **Status**: Not implemented
- **Solution**: Consolidate service functionality or clearly define responsibilities.

### Infrastructure Layer Issues ⚠️

#### Controller Layer Issues ❌
- **Issue**: Controllers directly use domain entities and lack proper DTO mapping
- **Status**: Not implemented
- **Solution**: 
  - Create proper DTOs and mappers
  - Implement standardized response format
  - Add validation annotations to DTOs

#### Anonymous Login Implementation ❌
- **Issue**: Anonymous login creates an empty `UserDetailsEntity` without proper initialization.
- **Status**: Not implemented
- **Solution**: Properly initialize the anonymous user:

```java
authenticatedUser = UserDetailsEntity.builder()
    .cpf("anonymous")
    .role(IamRole.CONSUMER)
    .build();
```

#### Inconsistent Response DTOs ❌
- **Issue**: The API documentation shows different response structures than what the code actually returns.
- **Status**: Not implemented
- **Solution**: Align response DTOs with API documentation or update the documentation.

#### Missing Input Validation ❌
- **Issue**: No validation for input DTOs like `RegisterUserDto` and `LoginUserDto`.
- **Status**: Not implemented
- **Solution**: Add validation annotations and implement validation logic.

### Security Configuration Issues ⚠️

#### JWT Authentication Filter Username Extraction ❌
- **Issue**: The filter extracts `userEmail` but uses it as a username.
- **Status**: Not implemented
- **Solution**: Rename the variable or adjust the extraction logic:

```java
final String userCpf = jwtService.extractUsername(jwt);
// Then use userCpf consistently
```

#### Security Configuration Permits All Requests ❌
- **Issue**: The security configuration has `.anyRequest().permitAll()` which effectively bypasses security.
- **Status**: Not implemented
- **Solution**: Implement proper authorization rules:

```java
.anyRequest().authenticated()
```

#### Missing Authority Implementation ❌
- **Issue**: `UserDetailsEntity.getAuthorities()` returns an empty list.
- **Status**: Not implemented
- **Solution**: Implement proper authorities:

```java
@Override
public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
}
```

## Configuration Improvements

### Identified Hardcoded Values

#### OpenApiConfig.java ⚠️

The `OpenApiConfig` class contains hardcoded values that should be externalized:

```java
@Value("Snackbar")
public String appTitle;

@Value("description")
public String appDescription;

@Value("1.0")
public String appVersion;
```

**Recommended Changes:**
- Replace with environment variable references:
  ```java
  @Value("${openapi.app.title:Snackbar}")
  public String appTitle;

  @Value("${openapi.app.description:Snackbar Management API}")
  public String appDescription;

  @Value("${openapi.app.version:1.0}")
  public String appVersion;
  ```

- Add corresponding properties to application.properties:
  ```properties
  openapi.app.title=Snackbar
  openapi.app.description=Snackbar Management API
  openapi.app.version=1.0
  ```

- Add environment variables to .env file:
  ```
  OPENAPI_APP_TITLE=Snackbar
  OPENAPI_APP_DESCRIPTION=Snackbar Management API
  OPENAPI_APP_VERSION=1.0
  ```

#### JwtService.java ✅

The `JwtService` class already uses environment variables for key configuration values:

```java
@Value("${security.jwt.secret-key}")
private String secretKey;

@Value("${security.jwt.expiration-time}")
private long jwtExpiration;
```

These are properly externalized, but we should ensure consistent naming conventions.

**Recommended Changes:**
- Consider renaming properties to follow AWS_* pattern for consistency:
  ```properties
  security.jwt.secret-key=${JWT_SECRET}
  security.jwt.expiration-time=${JWT_EXPIRES}
  ```

### Additional Configuration Considerations

1. **Security Configuration** ⚠️
   - Review any hardcoded security settings in `SecurityConfiguration.java`
   - Ensure CORS settings are configurable via environment variables

2. **User Service** ⚠️
   - Check for any hardcoded user roles or permissions
   - Ensure password policy settings are configurable

3. **Authentication Controller** ⚠️
   - Review for any hardcoded authentication settings like token refresh times

## Environment Variables Documentation

As part of the overall consolidation plan, the following environment variable documentation should be included for the IAM module:

```bash
# IAM Security Configuration
JWT_SECRET=                           # Secret key for JWT token signing
JWT_EXPIRES=86400000                  # JWT token expiration time in milliseconds (default: 24 hours)
JWT_ISSUER=snackbar-api               # JWT issuer name
JWT_AUDIENCE=snackbar-clients         # JWT audience name

# IAM Password Policy
IAM_PASSWORD_MIN_LENGTH=8             # Minimum password length
IAM_PASSWORD_REQUIRE_SPECIAL=true     # Require special characters in passwords
IAM_PASSWORD_REQUIRE_NUMBERS=true     # Require numbers in passwords
IAM_PASSWORD_REQUIRE_UPPERCASE=true   # Require uppercase letters in passwords

# IAM CORS Configuration
IAM_CORS_ALLOWED_ORIGINS=*            # Comma-separated list of allowed origins
IAM_CORS_ALLOWED_METHODS=GET,POST,PUT,DELETE,OPTIONS  # Allowed HTTP methods
IAM_CORS_ALLOWED_HEADERS=*            # Allowed HTTP headers
IAM_CORS_MAX_AGE=3600                 # Max age for CORS preflight requests in seconds

# OpenAPI Configuration
OPENAPI_APP_TITLE=Snackbar            # OpenAPI documentation title
OPENAPI_APP_DESCRIPTION=Snackbar Management API  # OpenAPI documentation description
OPENAPI_APP_VERSION=1.0               # OpenAPI documentation version
```

## Revised Implementation Priority

Based on the analysis of the product module's clean architecture implementation and the current state of the IAM module, the following revised implementation priority is recommended:

### 1. Establish Clean Architecture Foundation

**1.1. Create Domain Layer**
- Create domain exceptions package
  - `UserNotFoundException`
  - `InvalidCredentialsException`
  - `DuplicateUserException`
  - `InvalidUserDataException`
- Create consolidated domain entity with validation
  - Merge `UserEntity` and `UserDetailsEntity` into a single `User` class
  - Implement validation in the constructor
  - Implement `UserDetails` interface
- Create domain events
  - `UserCreatedEvent`
  - `UserUpdatedEvent`
  - `UserDeletedEvent`

**1.2. Create Application Layer**
- Define input ports (use case interfaces)
  - `RegisterUserInputPort`
  - `AuthenticateUserInputPort`
  - `GetUserByCpfInputPort`
  - `GetAllUsersInputPort`
  - `DeleteUserInputPort`
- Define output ports
  - `DomainEventPublisher`
- Define gateway interfaces
  - `UserGateway`
- Implement use cases
  - `RegisterUserUseCase`
  - `AuthenticateUserUseCase`
  - `GetUserByCpfUseCase`
  - `GetAllUsersUseCase`
  - `DeleteUserUseCase`

**1.3. Restructure Infrastructure Layer**
- Create persistence package
  - `UserEntity` (persistence entity)
  - `UserRepository` (Spring Data repository)
- Create gateway implementations
  - `UserRepositoryGateway`
  - `UserEntityMapper`
- Create controllers package
  - Refactor existing controllers
  - Create DTOs and mappers
- Create configuration package
  - Move security configuration
  - Create dependency injection configuration

### 2. Implement Security Improvements

**2.1. Fix JWT Authentication**
- Refactor `JwtService` to use proper naming
- Ensure consistent use of CPF as username
- Implement proper token validation

**2.2. Implement Proper Authorities**
- Ensure `User` entity returns proper authorities
- Configure role-based access control

**2.3. Configure Security Rules**
- Replace `.anyRequest().permitAll()` with proper authorization rules
- Implement endpoint security based on roles

### 3. Enhance Validation and Error Handling

**3.1. Implement Comprehensive Validation**
- Add validation for CPF format
- Add validation for email format
- Add password strength requirements
- Add role validation

**3.2. Standardize Error Responses**
- Create global exception handler
- Implement standardized response format
- Map exceptions to appropriate HTTP status codes

### 4. Externalize Configuration

**4.1. Identify Hardcoded Values**
- Security settings
- Validation rules
- Application properties

**4.2. Create Configuration Classes**
- Security configuration
- Validation configuration
- Application configuration

**4.3. Use Environment Variables**
- JWT settings
- Password policy
- CORS settings

### 5. Add Comprehensive Tests

**5.1. Unit Tests**
- Domain entity tests
- Use case tests
- Service tests

**5.2. Integration Tests**
- Controller tests
- Repository tests
- Security tests

**5.3. End-to-End Tests**
- API tests
- Authentication flow tests

## Implementation Strategy

### Package Structure

The IAM module should follow this package structure to align with clean architecture principles:

```
com.snackbar.iam
├── domain
│   ├── entity
│   │   └── User.java
│   ├── exceptions
│   │   ├── UserNotFoundException.java
│   │   ├── InvalidCredentialsException.java
│   │   ├── DuplicateUserException.java
│   │   └── InvalidUserDataException.java
│   └── event
│       ├── UserCreatedEvent.java
│       ├── UserUpdatedEvent.java
│       └── UserDeletedEvent.java
├── application
│   ├── ports
│   │   ├── in
│   │   │   ├── RegisterUserInputPort.java
│   │   │   ├── AuthenticateUserInputPort.java
│   │   │   ├── GetUserByCpfInputPort.java
│   │   │   ├── GetAllUsersInputPort.java
│   │   │   └── DeleteUserInputPort.java
│   │   └── out
│   │       └── DomainEventPublisher.java
│   ├── gateways
│   │   └── UserGateway.java
│   └── usecases
│       ├── RegisterUserUseCase.java
│       ├── AuthenticateUserUseCase.java
│       ├── GetUserByCpfUseCase.java
│       ├── GetAllUsersUseCase.java
│       └── DeleteUserUseCase.java
└── infrastructure
    ├── controllers
    │   ├── AuthenticationController.java
    │   ├── UserController.java
    │   ├── GlobalExceptionHandler.java
    │   ├── UserDTOMapper.java
    │   └── dto
    │       ├── RegisterUserRequestDTO.java
    │       ├── LoginRequestDTO.java
    │       ├── UserResponseDTO.java
    │       └── ResponseDTO.java
    ├── persistence
    │   ├── UserEntity.java
    │   └── UserRepository.java
    ├── gateways
    │   ├── UserRepositoryGateway.java
    │   └── UserEntityMapper.java
    ├── security
    │   ├── JwtService.java
    │   └── JwtAuthenticationFilter.java
    └── config
        ├── IamConfig.java
        ├── SecurityConfig.java
        └── OpenApiConfig.java
```

### Implementation Approach

1. **Incremental Implementation**:
   - Start with the domain layer
   - Then implement the application layer
   - Finally refactor the infrastructure layer

2. **Feature-by-Feature Migration**:
   - Begin with user registration
   - Then implement authentication
   - Finally implement user management

3. **Continuous Testing**:
   - Write tests for each component
   - Ensure backward compatibility
   - Validate against existing functionality

4. **Documentation**:
   - Update API documentation
   - Document architecture decisions
   - Create developer guidelines

## Benefits

- **Improved Security**: Proper implementation of authentication and authorization
- **Better Maintainability**: Clean architecture makes the code easier to maintain and extend
- **Reduced Technical Debt**: Proper architecture reduces long-term maintenance costs
- **Consistent Error Handling**: Standardized error responses improve API usability
- **Configurable Security Settings**: Externalized configuration allows changing security settings without code changes
- **Better Testability**: Proper separation of concerns enables better unit testing
- **Simplified Configuration**: One approach for all environments
- **Explicit Settings**: No hidden defaults or environment-specific behavior
- **Improved Onboarding**: Clear documentation for new team members
- **Reduced Errors**: Consistent naming and structure prevents mistakes

## Best Practices for Testing

Ensure proper cleanup between test runs:
- `docker compose down -v --rmi all` to remove all containers, volumes and images
- `rm -rf backend/target` to clean build artifacts
- `mvn -f backend/pom.xml clean package` to rebuild the application
- `docker compose up --build -d` to rebuild and start containers
