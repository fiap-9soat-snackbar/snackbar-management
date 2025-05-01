# IAM Module Revised Improvements

This document consolidates the analysis, implementation status, and improvement plan for the IAM module in the Snackbar Management application, incorporating clean architecture principles based on the product module implementation.

## Progress Summary (Updated: April 30, 2025)

- **Domain Layer**: 80% Complete
  - User entity ✅
  - Domain exceptions ✅
  - Domain events ✅

- **Application Layer**: 60% Complete
  - Input ports ✅
  - Use cases ✅
  - Gateways ✅
  - Event publishers ⚠️ (Partially implemented)

- **Infrastructure Layer**: 80% Complete
  - Controllers ✅
  - DTOs ✅
  - Repositories ✅
  - Error handling ✅
  - Security integration ✅

## Hybrid Implementation Approach

To balance clean architecture principles with maintaining functionality, we've adopted a hybrid approach:

1. **Core Clean Architecture Components**: Implementing the full clean architecture structure as the target state
2. **Temporary Adapter Components**: Creating clearly marked adapters to bridge between new and legacy components
3. **Migration Plan**: Establishing a clear path to gradually remove legacy components

This approach allows us to maintain the clean architecture vision while ensuring the system remains functional during the transition.

## Current Status Assessment

### API Endpoints ✅
All endpoints have been implemented:
- POST /api/v2/user/auth/signup ✅
- POST /api/v2/user/auth/login ✅
- GET /api/v2/user/ ✅
- GET /api/v2/user/cpf/{cpf} ✅
- DELETE /api/v2/user/{id} ✅

### Error Handling ✅
- **Current Coverage**: 100%
  - Domain-specific exceptions implemented ✅
  - Global exception handler implemented ✅
  - Proper HTTP status codes ✅
  - Consistent error response format ✅
  - Exception handling for authentication failures ✅
- **Remaining Work**: None

### Field Validation ✅
- **Current Coverage**: 100%
  - CPF format validation ✅
  - Email format validation ✅
  - Password strength requirements ✅
  - Role validation ✅
- **Remaining Work**: None

## Clean Architecture Analysis

The current IAM module has several architectural issues when compared to the product module's clean architecture implementation:

### Domain Layer Issues ✅

#### Entity Duplication and Inconsistency ✅
- **Issue**: Two nearly identical entities (`UserEntity` and `UserDetailsEntity`) represent the same domain concept.
- **Status**: Implemented
- **Solution**: Consolidated into a single `User` domain entity.
- **Date**: April 29, 2025

#### Field Name Inconsistency ✅
- **Issue**: In `UserEntity`, the field is named `name` but in the API documentation and DTOs it's referred to as `fullName`.
- **Status**: Implemented
- **Solution**: Standardized naming across all layers.
- **Date**: April 29, 2025

#### Missing Domain Events ✅
- **Issue**: No domain events for important state changes
- **Status**: Implemented
- **Solution**: Created domain events for user lifecycle:
  - `UserCreatedEvent` ✅
  - `UserUpdatedEvent` ✅
  - `UserDeletedEvent` ✅
- **Date**: April 29, 2025

### Application Layer Issues ⚠️

#### Missing Use Case Interfaces ✅
- **Issue**: No clear separation between use case definition and implementation
- **Status**: Implemented
- **Solution**: Created input ports (interfaces) for all use cases:
  - `RegisterUserInputPort` ✅
  - `AuthenticateUserInputPort` ✅
  - `GetUserByCpfInputPort` ✅
  - `GetAllUsersInputPort` ✅
  - `DeleteUserInputPort` ✅
- **Date**: April 29, 2025

#### Repository Type Mismatch ✅
- **Issue**: `IamRepository` is defined for `UserEntity` but returns `UserDetailsEntity`.
- **Status**: Implemented
- **Solution**: Created a proper gateway interface and implementation.
- **Date**: April 29, 2025

#### Redundant Repositories ✅
- **Issue**: Both `IamRepository` and `UserRepository` serve identical purposes.
- **Status**: Implemented
- **Solution**: Consolidated into a single repository interface with proper naming.
- **Date**: April 29, 2025

#### Inconsistent Return Types ✅
- **Issue**: `AuthenticationService.authenticate()` returns `UserDetailsEntity` but `signup()` returns `UserEntity`.
- **Status**: Implemented
- **Solution**: Standardized return types across service methods.
- **Date**: April 29, 2025

#### Missing Error Handling ✅
- **Issue**: `UserService.getUserByCpf()` uses `orElseThrow()` without specifying an exception.
- **Status**: Implemented
- **Solution**: Provided specific exceptions.
- **Date**: April 29, 2025

#### Dependency on External Service ❌
- **Issue**: `AuthenticationController` depends on `OrderService` which is outside the IAM module.
- **Status**: Not implemented
- **Solution**: Remove this dependency or use an interface/port to maintain loose coupling.

#### Redundant Service Implementations ✅
- **Issue**: `IamService` and `IamServiceImpl` exist but are barely used, while most functionality is in `AuthenticationService`.
- **Status**: Implemented
- **Solution**: Consolidated service functionality with clear responsibilities.
- **Date**: April 29, 2025

### Infrastructure Layer Issues ✅

#### Controller Layer Issues ✅
- **Issue**: Controllers directly use domain entities and lack proper DTO mapping
- **Status**: Implemented
- **Solution**: 
  - Created proper DTOs and mappers ✅
  - Implemented standardized response format ✅
  - Added validation annotations to DTOs ✅
- **Date**: April 29, 2025

#### Anonymous Login Implementation ✅
- **Issue**: Anonymous login creates an empty `UserDetailsEntity` without proper initialization.
- **Status**: Implemented
- **Solution**: Properly initialized the anonymous user.
- **Date**: April 29, 2025

#### Inconsistent Response DTOs ✅
- **Issue**: The API documentation shows different response structures than what the code actually returns.
- **Status**: Implemented
- **Solution**: Aligned response DTOs with API documentation.
- **Date**: April 29, 2025

#### Missing Input Validation ✅
- **Issue**: No validation for input DTOs like `RegisterUserDto` and `LoginUserDto`.
- **Status**: Implemented
- **Solution**: Added validation annotations and implemented validation logic.
- **Date**: April 29, 2025

### Security Configuration Issues ✅

#### JWT Authentication Filter Username Extraction ✅
- **Issue**: The filter extracts `userEmail` but uses it as a username.
- **Status**: Implemented
- **Solution**: Renamed variables and adjusted extraction logic to use CPF consistently.
- **Date**: April 30, 2025

#### Security Configuration Permits All Requests ✅
- **Issue**: The security configuration has `.anyRequest().permitAll()` which effectively bypasses security.
- **Status**: Implemented
- **Solution**: Implemented proper authorization rules with specific URL patterns.
- **Date**: April 30, 2025

#### Missing Authority Implementation ✅
- **Issue**: `UserDetailsEntity.getAuthorities()` returns an empty list.
- **Status**: Implemented
- **Solution**: Implemented proper authorities based on user roles.
- **Date**: April 30, 2025

## Integration Issues

The following integration issues have been identified:

1. **JWT Token Validation**: ✅
   - Error: JWT signature does not match locally computed signature
   - Root cause: Different secret keys or token formats between generation and validation
   - Status: Fixed
   - Solution: Added proper error handling and validation in JWT service
   - Date: April 30, 2025

2. **Spring Security Integration**: ✅
   - Error: UsernameNotFoundException for valid CPFs
   - Root cause: Disconnect between domain User entity and Spring Security's UserDetailsService
   - Status: Fixed
   - Solution: Created proper adapter between domain User and Spring Security UserDetails
   - Date: April 30, 2025

3. **User Deletion and Token Validation**: ✅
   - Error: Authentication errors when using tokens for deleted users
   - Root cause: No handling for the case when a user is deleted but their token is still valid
   - Status: Fixed
   - Solution: Added graceful handling in JWT filter for deleted users
   - Date: April 30, 2025

## Revised Implementation Priority

Based on our hybrid approach, the following implementation priority is recommended:

### Phase 1: Complete Core Clean Architecture Components (Current Focus)

**1.1. Fix Integration Issues** ✅
- Create UserDetailsService adapter to bridge domain and Spring Security ✅
- Ensure consistent JWT handling ✅
- Fix token validation issues ✅

**1.2. Complete Domain Events** ✅
- Implement remaining domain events ✅
- Ensure proper event publishing ⚠️

**1.3. Enhance Validation and Error Handling** ✅
- Implement comprehensive input validation ✅
  - Add validation annotations to DTOs ✅
  - Implement custom validators for complex rules ✅
  - Add validation for business rules ✅
- Standardize error responses ✅
  - Create consistent error response format ✅
  - Map exceptions to appropriate HTTP status codes ✅
  - Add error codes for client-side handling ✅
- Implement global exception handling ✅
  - Create centralized exception handler ✅
  - Add logging for all exceptions ✅
  - Provide user-friendly error messages ✅

**1.4. Implement Security Improvements** ✅
- Fix JWT Authentication Filter username extraction ✅
- Configure proper security rules ✅
- Implement proper authorities ✅
  - Map user roles to Spring Security authorities ✅
  - Implement role-based access control ✅
  - Add method-level security annotations ✅

### Phase 2: Create Temporary Adapter Components ✅

**2.1. Create Clearly Marked Adapter Classes** ✅
- Implement adapters between new and legacy components ✅
- Document temporary nature of adapters ✅

**2.2. Implement Proper Authorities** ✅
- Create role-based authority mapping ✅
- Implement proper `getAuthorities()` method ✅
- Add authority checks to secured endpoints ✅

**2.3. Configure Security Rules** ✅
- Replace `.anyRequest().permitAll()` with proper rules ✅
- Implement endpoint-specific security rules ✅
- Add CSRF protection ✅
- Configure proper CORS settings ✅

**2.4. Externalize Configuration** ⚠️
- Identify hardcoded values ✅
  - Review all classes for hardcoded configuration ✅
  - Document all configuration parameters ✅
  - Create a configuration inventory ✅
- Create configuration classes ✅
  - Implement proper configuration properties classes ✅
  - Add validation for configuration values ✅
  - Provide sensible defaults ✅
- Use environment variables ⚠️
  - Map configuration to environment variables ⚠️
  - Document required environment variables ⚠️
  - Implement configuration validation on startup ✅

**2.5. Ensure Backward Compatibility** ✅
- Verify all existing functionality works ✅
- Add tests for compatibility ✅

### Phase 3: Migration Plan for Legacy Components ⚠️

**3.1. Document Dependencies** ⚠️
- Map which legacy components are used where ⚠️
- Identify all integration points ⚠️

**3.2. Define Removal Criteria** ❌
- Set conditions for when each legacy component can be removed ❌
- Create tests to verify requirements ❌

**3.3. Establish Timeline** ❌
- Set target dates for completing each phase ❌
- Plan incremental migrations ❌

**3.4. Add Comprehensive Tests** ⚠️
- Unit tests ⚠️
  - Test domain entities and validation ⚠️
  - Test use cases in isolation ⚠️
  - Test mappers and converters ⚠️
- Integration tests ✅
  - Test repository implementations ✅
  - Test controller endpoints ✅
  - Test security configuration ✅
- End-to-end tests ❌
  - Test complete user flows ❌
  - Test error scenarios ❌
  - Test performance under load ❌
- Security tests ⚠️
  - Test authentication flows ✅
  - Test authorization rules ✅
  - Test for common security vulnerabilities ❌

## Package Structure

The IAM module now follows this package structure to align with clean architecture principles:

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
│   │       └── IamDomainEventPublisher.java
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
    │   ├── UserAuthController.java
    │   ├── UserMgmtController.java
    │   ├── IamGlobalExceptionHandler.java
    │   ├── UserDTOMapper.java
    │   └── dto
    │       ├── RegisterUserRequestDTO.java
    │       ├── LoginRequestDTO.java
    │       ├── UserResponseDTO.java
    │       └── IamErrorResponseDTO.java
    ├── persistence
    │   ├── UserEntity.java
    │   └── UserRepository.java
    ├── gateways
    │   ├── UserRepositoryGateway.java
    │   └── UserEntityMapper.java
    ├── security
    │   ├── JwtService.java
    │   ├── UserDetailsAdapter.java
    │   └── IamJwtAuthenticationFilter.java
    ├── adapter
    │   └── UserDetailsServiceAdapter.java
    └── config
        ├── IamConfig.java
        ├── IamSecurityConfig.java
        └── IamAuthenticationConfig.java
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

These are properly externalized, and now include validation to ensure the secret key is properly configured.

### Additional Configuration Considerations

1. **Security Configuration** ✅
   - Security settings in `IamSecurityConfig.java` are now properly configured
   - CORS settings should be made configurable via environment variables

2. **User Service** ✅
   - User roles are now properly handled
   - Password policy settings should be made configurable

3. **Authentication Controller** ✅
   - Authentication settings like token refresh times are now configurable

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

## Implementation Approach

1. **Incremental Implementation**:
   - Start with the domain layer ✅
   - Then implement the application layer ✅
   - Finally refactor the infrastructure layer ✅

2. **Feature-by-Feature Migration**:
   - Begin with user registration ✅
   - Then implement authentication ✅
   - Finally implement user management ✅

3. **Continuous Testing**:
   - Write tests for each component ⚠️
   - Ensure backward compatibility ✅
   - Validate against existing functionality ✅

4. **Documentation**:
   - Update API documentation ✅
   - Document architecture decisions ✅
   - Create developer guidelines ⚠️

## Benefits

- **Improved Security**: Proper implementation of authentication and authorization ✅
- **Better Maintainability**: Clean architecture makes the code easier to maintain and extend ✅
- **Reduced Technical Debt**: Proper architecture reduces long-term maintenance costs ✅
- **Consistent Error Handling**: Standardized error responses improve API usability ✅
- **Configurable Security Settings**: Externalized configuration allows changing security settings without code changes ✅
- **Better Testability**: Proper separation of concerns enables better unit testing ✅
- **Simplified Configuration**: One approach for all environments ✅
- **Explicit Settings**: No hidden defaults or environment-specific behavior ✅
- **Improved Onboarding**: Clear documentation for new team members ⚠️
- **Reduced Errors**: Consistent naming and structure prevents mistakes ✅

## Best Practices for Testing

Ensure proper cleanup between test runs:
- `docker compose down -v --rmi all` to remove all containers, volumes and images
- `rm -rf backend/target` to clean build artifacts
- `mvn -f backend/pom.xml clean package` to rebuild the application
- `docker compose up --build -d` to rebuild and start containers
