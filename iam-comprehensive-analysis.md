# Comprehensive IAM Module Analysis

## Compilation Errors and Clean Architecture Issues

### 0. Critical Compilation Errors

#### 0.1 Missing Exception Class Import
- **Issue**: In `AuthenticationService` line 3, the code imports `UserNotFoundException` from the cooking module:
  ```java
  import com.snackbar.cooking.domain.exceptions.UserNotFoundException;
  ```
- **Compilation Error**: This will cause a compilation failure as the imported class cannot be found.
- **Clean Architecture Violation**: This violates module boundaries by attempting to import from another module's domain layer.
- **Solution**: Create a proper exception class within the IAM module:
  ```java
  package com.snackbar.iam.domain.exceptions;
  
  public class UserNotFoundException extends RuntimeException {
      public UserNotFoundException(String message) {
          super(message);
      }
  }
  ```

#### 0.2 Missing Exception Class Usage
- **Issue**: In `AuthenticationService` line 56, the code attempts to throw a `UserNotFoundException` that doesn't exist:
  ```java
  return userRepository.findByCpf(cpf)
      .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado para o CPF: " + cpf));
  ```
- **Compilation Error**: This will cause a compilation failure as the exception class cannot be found.
- **Clean Architecture Violation**: Each module should define its own exceptions rather than depending on exceptions from other modules.
- **Solution**: Create the missing exception class as shown in the solution for issue #0.1.

#### 0.3 Type Mismatch Error
- **Issue**: In `AuthenticationService` line 56, `userRepository.findByCpf()` returns `Optional<UserEntity>` but the method signature expects to return `UserDetailsEntity`.
- **Compilation Error**: This will cause a type mismatch error as the repository returns the wrong type.
- **Clean Architecture Violation**: This inconsistency shows a lack of proper domain modeling and separation of concerns.
- **Solution**: Either consolidate the entity types or add a conversion between entity types.

### 1. Entity Layer Issues

#### 1.1 Entity Duplication and Inconsistency
- **Issue**: Two nearly identical entities (`UserEntity` and `UserDetailsEntity`) represent the same domain concept.
- **Clean Architecture Violation**: Domain entities should be cohesive and represent a single concept.
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

#### 1.2 Field Name Inconsistency
- **Issue**: In `UserEntity`, the field is named `name` but in the API documentation and DTOs it's referred to as `fullName`.
- **Solution**: Standardize naming across all layers.

### 2. Repository Layer Issues

#### 2.1 Repository Type Mismatch
- **Issue**: `IamRepository` is defined for `UserEntity` but returns `UserDetailsEntity`.
- **Compilation Error**: This will cause type casting errors at runtime.
- **Solution**: Align repository return types with entity types.

```java
public interface IamRepository extends MongoRepository<UserEntity, String> {
    Optional<UserEntity> findByEmail(String email);
    Optional<UserEntity> findByCpf(String cpf);
}
```

#### 2.2 Redundant Repositories
- **Issue**: Both `IamRepository` and `UserRepository` serve identical purposes.
- **Clean Architecture Violation**: Violates the DRY principle and creates confusion about which repository to use.
- **Solution**: Consolidate into a single repository interface.

### 3. Service Layer Issues

#### 3.1 Inconsistent Return Types
- **Issue**: `AuthenticationService.authenticate()` returns `UserDetailsEntity` but `signup()` returns `UserEntity`.
- **Compilation Error**: This inconsistency will cause type casting errors.
- **Solution**: Standardize return types across service methods.

#### 3.2 Missing Error Handling
- **Issue**: `UserService.getUserByCpf()` uses `orElseThrow()` without specifying an exception.
- **Compilation Error**: This will throw a `NoSuchElementException` without proper context.
- **Solution**: Provide a specific exception:

```java
.orElseThrow(() -> new UserNotFoundException("User not found with CPF: " + cpf));
```

#### 3.3 Dependency on External Service
- **Issue**: `AuthenticationController` depends on `OrderService` which is outside the IAM module.
- **Clean Architecture Violation**: This violates module boundaries and creates tight coupling.
- **Solution**: Remove this dependency or use an interface/port to maintain loose coupling.

#### 3.4 Redundant Service Implementations
- **Issue**: `IamService` and `IamServiceImpl` exist but are barely used, while most functionality is in `AuthenticationService`.
- **Clean Architecture Violation**: Service responsibilities are not clearly defined.
- **Solution**: Consolidate service functionality or clearly define responsibilities.

### 4. Controller Layer Issues

#### 4.1 Anonymous Login Implementation
- **Issue**: Anonymous login creates an empty `UserDetailsEntity` without proper initialization.
- **Compilation Error**: This will cause NullPointerExceptions when the JWT service tries to access properties.
- **Solution**: Properly initialize the anonymous user:

```java
authenticatedUser = UserDetailsEntity.builder()
    .cpf("anonymous")
    .role(IamRole.CONSUMER)
    .build();
```

#### 4.2 Inconsistent Response DTOs
- **Issue**: The API documentation shows different response structures than what the code actually returns.
- **Solution**: Align response DTOs with API documentation or update the documentation.

#### 4.3 Missing Input Validation
- **Issue**: No validation for input DTOs like `RegisterUserDto` and `LoginUserDto`.
- **Solution**: Add validation annotations and implement validation logic.

### 5. Security Configuration Issues

#### 5.1 JWT Authentication Filter Username Extraction
- **Issue**: The filter extracts `userEmail` but uses it as a username.
- **Compilation Error**: This is inconsistent with the `UserDetailsService` which loads by CPF.
- **Solution**: Rename the variable or adjust the extraction logic:

```java
final String userCpf = jwtService.extractUsername(jwt);
// Then use userCpf consistently
```

#### 5.2 Security Configuration Permits All Requests
- **Issue**: The security configuration has `.anyRequest().permitAll()` which effectively bypasses security.
- **Clean Architecture Violation**: Security is a cross-cutting concern that should be properly enforced.
- **Solution**: Implement proper authorization rules:

```java
.anyRequest().authenticated()
```

#### 5.3 Missing Authority Implementation
- **Issue**: `UserDetailsEntity.getAuthorities()` returns an empty list.
- **Clean Architecture Violation**: This prevents proper role-based access control.
- **Solution**: Implement proper authorities:

```java
@Override
public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
}
```

### 6. Clean Architecture Structural Issues

#### 6.1 Package Structure Inconsistency
- **Issue**: The application follows a hexagonal architecture but the package structure doesn't clearly reflect ports and adapters.
- **Clean Architecture Violation**: Package structure should reflect architectural boundaries.
- **Solution**: Reorganize packages to clearly separate:
  - Domain (core entities and business rules)
  - Application (use cases, ports)
  - Infrastructure (adapters, repositories)
  - Web (controllers, DTOs)

#### 6.2 Missing Use Case Interfaces
- **Issue**: Services are directly implemented without defining use case interfaces.
- **Clean Architecture Violation**: Use cases should be defined as interfaces in the application layer.
- **Solution**: Define interfaces for all use cases:

```java
public interface AuthenticationUseCase {
    UserEntity signup(RegisterUserDto input);
    UserEntity authenticate(LoginUserDto input);
    UserEntity findByCpf(String cpf);
}
```

#### 6.3 Direct Domain Entity Exposure
- **Issue**: Domain entities are directly exposed in controller responses.
- **Clean Architecture Violation**: Domain entities should be isolated from external layers.
- **Solution**: Use DTOs consistently for all external communication.

## Recommended Refactoring Steps

1. **Consolidate Domain Entities**: Merge `UserEntity` and `UserDetailsEntity` into a single class.

2. **Standardize Repository Layer**: Consolidate repositories and ensure consistent return types.

3. **Define Clear Use Case Interfaces**: Create interfaces for all application services.

4. **Implement Proper DTOs**: Ensure all external communication uses DTOs, not domain entities.

5. **Fix Security Implementation**: Properly implement authorities and security rules.

6. **Add Input Validation**: Implement validation for all input DTOs.

7. **Reorganize Package Structure**: Align package structure with hexagonal architecture principles.

8. **Remove External Dependencies**: Eliminate dependencies that cross module boundaries.

By addressing these issues, the IAM module will better adhere to clean architecture principles, have fewer compilation errors, and be more maintainable.
