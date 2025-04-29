# IAM Module Improvements

This document consolidates the analysis, implementation status, and configuration improvements for the IAM module in the Snackbar Management application.

## Implementation Status

### 1. API Endpoints ✅
All endpoints have been implemented:
- POST /api/user/auth/signup ✅
- POST /api/user/auth/login ✅
- GET /api/user/ ✅
- GET /api/user/cpf/{cpf} ✅
- DELETE /api/user/{id} ✅

### 2. Error Handling ⚠️
- **Current Coverage**: ~50%
  - Missing proper exception handling for authentication failures
  - Missing domain-specific exceptions
- **Expected Coverage**: 100%
  - Need to create domain-specific exceptions:
    - `UserNotFoundException`
    - `InvalidCredentialsException`
    - `DuplicateUserException`
  - Need to implement proper error responses with appropriate HTTP status codes

### 3. Field Validation ⚠️
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

## Clean Architecture Implementation

### Domain Layer Issues ⚠️

#### 1. Entity Duplication and Inconsistency ❌
- **Issue**: Two nearly identical entities (`UserEntity` and `UserDetailsEntity`) represent the same domain concept.
- **Status**: Not implemented
- **Solution**: Consolidate into a single entity that implements `UserDetails` interface.

```java
@Document(collection = "user")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEntity implements UserDetails {
    @Id
    private String id;
    private String name;
    private String email;
    private String cpf;
    private IamRole role;
    private String password;
    
    // UserDetails implementation methods
}
```

#### 2. Field Name Inconsistency ❌
- **Issue**: In `UserEntity`, the field is named `name` but in the API documentation and DTOs it's referred to as `fullName`.
- **Status**: Not implemented
- **Solution**: Standardize naming across all layers.

### Repository Layer Issues ⚠️

#### 1. Repository Type Mismatch ❌
- **Issue**: `IamRepository` is defined for `UserEntity` but returns `UserDetailsEntity`.
- **Status**: Not implemented
- **Solution**: Align repository return types with entity types.

```java
public interface IamRepository extends MongoRepository<UserEntity, String> {
    Optional<UserEntity> findByEmail(String email);
    Optional<UserEntity> findByCpf(String cpf);
}
```

#### 2. Redundant Repositories ❌
- **Issue**: Both `IamRepository` and `UserRepository` serve identical purposes.
- **Status**: Not implemented
- **Solution**: Consolidate into a single repository interface.

### Service Layer Issues ⚠️

#### 1. Inconsistent Return Types ❌
- **Issue**: `AuthenticationService.authenticate()` returns `UserDetailsEntity` but `signup()` returns `UserEntity`.
- **Status**: Not implemented
- **Solution**: Standardize return types across service methods.

#### 2. Missing Error Handling ❌
- **Issue**: `UserService.getUserByCpf()` uses `orElseThrow()` without specifying an exception.
- **Status**: Not implemented
- **Solution**: Provide a specific exception:

```java
.orElseThrow(() -> new UserNotFoundException("User not found with CPF: " + cpf));
```

#### 3. Dependency on External Service ❌
- **Issue**: `AuthenticationController` depends on `OrderService` which is outside the IAM module.
- **Status**: Not implemented
- **Solution**: Remove this dependency or use an interface/port to maintain loose coupling.

#### 4. Redundant Service Implementations ❌
- **Issue**: `IamService` and `IamServiceImpl` exist but are barely used, while most functionality is in `AuthenticationService`.
- **Status**: Not implemented
- **Solution**: Consolidate service functionality or clearly define responsibilities.

### Controller Layer Issues ⚠️

#### 1. Anonymous Login Implementation ❌
- **Issue**: Anonymous login creates an empty `UserDetailsEntity` without proper initialization.
- **Status**: Not implemented
- **Solution**: Properly initialize the anonymous user:

```java
authenticatedUser = UserDetailsEntity.builder()
    .cpf("anonymous")
    .role(IamRole.CONSUMER)
    .build();
```

#### 2. Inconsistent Response DTOs ❌
- **Issue**: The API documentation shows different response structures than what the code actually returns.
- **Status**: Not implemented
- **Solution**: Align response DTOs with API documentation or update the documentation.

#### 3. Missing Input Validation ❌
- **Issue**: No validation for input DTOs like `RegisterUserDto` and `LoginUserDto`.
- **Status**: Not implemented
- **Solution**: Add validation annotations and implement validation logic.

### Security Configuration Issues ⚠️

#### 1. JWT Authentication Filter Username Extraction ❌
- **Issue**: The filter extracts `userEmail` but uses it as a username.
- **Status**: Not implemented
- **Solution**: Rename the variable or adjust the extraction logic:

```java
final String userCpf = jwtService.extractUsername(jwt);
// Then use userCpf consistently
```

#### 2. Security Configuration Permits All Requests ❌
- **Issue**: The security configuration has `.anyRequest().permitAll()` which effectively bypasses security.
- **Status**: Not implemented
- **Solution**: Implement proper authorization rules:

```java
.anyRequest().authenticated()
```

#### 3. Missing Authority Implementation ❌
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

#### 1. OpenApiConfig.java ⚠️

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

#### 2. JwtService.java ✅

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

## Consistent Naming Convention

The IAM module should follow these naming conventions for environment variables:

- Use uppercase with underscores (e.g., `JWT_SECRET`)
- Group related variables with common prefixes (e.g., `IAM_PASSWORD_*`)
- Use descriptive names that clearly indicate purpose
- Follow the pattern: `[MODULE]_[CATEGORY]_[PROPERTY]`

## Remaining Tasks

1. **Implement Domain-Specific Exceptions**: ❌
   - Create `UserNotFoundException`
   - Create `InvalidCredentialsException`
   - Create `DuplicateUserException`
   - Implement proper exception handling in controllers

2. **Consolidate Entity Classes**: ❌
   - Merge `UserEntity` and `UserDetailsEntity` into a single class
   - Implement `UserDetails` interface properly
   - Ensure consistent field naming

3. **Standardize Repository Layer**: ❌
   - Consolidate repositories
   - Ensure consistent return types
   - Add proper error handling

4. **Implement Proper Validation**: ❌
   - Add validation annotations to DTOs
   - Implement validation logic in domain entities
   - Add validation for CPF format, email format, and password strength

5. **Fix Security Implementation**: ❌
   - Implement proper authorities
   - Configure security rules correctly
   - Fix JWT authentication filter

6. **Externalize Configuration**: ⚠️
   - Implement the configuration improvements outlined above
   - Ensure all hardcoded values are externalized

7. **Add Unit Tests**: ❌
   - Implement comprehensive unit tests for all components
   - Test validation rules and error scenarios
   - Test security configuration

8. **Implement Clean Architecture**: ❌
   - Define use case interfaces
   - Separate domain logic from infrastructure concerns
   - Implement proper dependency inversion

## Implementation Priority

1. **Critical Security Fixes**
   - Fix JWT authentication filter
   - Implement proper authorities
   - Configure security rules correctly

2. **Domain Model Consolidation**
   - Consolidate entity classes
   - Standardize repository layer
   - Implement domain-specific exceptions

3. **Validation and Error Handling**
   - Implement proper validation
   - Add comprehensive error handling
   - Standardize response DTOs

4. **Configuration Externalization**
   - Externalize all hardcoded values
   - Implement consistent naming conventions
   - Document environment variables

5. **Clean Architecture Implementation**
   - Define use case interfaces
   - Implement proper dependency inversion
   - Separate domain logic from infrastructure concerns

6. **Testing and Documentation**
   - Add unit tests
   - Document API endpoints
   - Add code documentation

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
