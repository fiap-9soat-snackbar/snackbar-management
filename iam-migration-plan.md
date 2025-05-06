# IAM Module Legacy Code Removal Plan

This document outlines a detailed plan for removing legacy code from the IAM module and completing the migration to a clean architecture structure that aligns with the product module. The plan is based on the current state of the codebase, existing adapters, and dependencies between layers.

## Current State Analysis

The IAM module is currently in a hybrid state with:

1. **Clean Architecture Components** (already implemented):
   - Domain entities (`User.java`)
   - Domain events (`UserCreatedEvent.java`, `UserUpdatedEvent.java`, `UserDeletedEvent.java`)
   - Input/output ports
   - Use cases
   - Gateway interfaces
   - Infrastructure implementations including:
     - Controllers (`UserAuthController.java`, `UserMgmtController.java`)
     - DTOs in infrastructure.controllers.dto
     - Security components (`IamJwtAuthenticationFilter.java`, `JwtService.java`)
     - Configuration (`IamSecurityConfig.java`, `IamAuthenticationConfig.java`)
     - Persistence (`UserEntity.java`, `UserRepository.java` in infrastructure.persistence)

2. **Legacy Components** (to be removed):
   - ✅ `web` package with controllers (REMOVED)
   - ✅ `web` package with DTOs (REMOVED)
   - `UserEntity.java` in domain layer
   - `UserDetailsEntity.java` in domain layer
   - `AuthenticationService.java` and `UserService.java` in application layer
   - `SecurityConfiguration.java` and `SecurityConfigurationAdapter.java` in application layer
   - `JwtAuthenticationFilter.java` in application layer
   - `IamRepository.java` and `UserRepository.java` in infrastructure root

3. **Adapter Components** (temporary):
   - `UserEntityAdapter.java` in domain.adapter
   - `AuthenticationServiceAdapter.java` and `UserServiceAdapter.java` in application.adapter
   - `UserRepositoryAdapter.java`, `IamRepositoryAdapter.java`, `UserDetailsServiceAdapter.java` in infrastructure.adapter

## Target Package Structure

The target structure is already largely implemented, with the following components in place:

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
│       ├── UserDeletedEvent.java
│       └── UserDomainEvent.java
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
    │       ├── AuthenticationResponseDTO.java
    │       ├── UpdateUserRequestDTO.java
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
    │   ├── IamJwtAuthenticationFilter.java
    │   └── exception
    │       └── [Security exceptions]
    └── config
        ├── IamConfig.java
        ├── IamSecurityConfig.java
        └── IamAuthenticationConfig.java
```

## Migration Strategy

The migration will follow the natural dependency direction (from outer layers to inner layers) while leveraging the adapter pattern to maintain functionality throughout the process:

1. **Start with Web Layer**: ✅ Remove legacy controllers and DTOs (COMPLETED)
2. **Infrastructure Layer**: Remove legacy repositories and infrastructure components
3. **Application Layer**: Remove legacy services and security components
4. **Domain Layer**: Remove legacy domain entities
5. **Remove Adapters**: Once all legacy components are removed, remove the adapter components

After each step, we will run `iam_test_integration_v3.sh` to verify that everything still works correctly.

## Detailed Migration Steps

### Phase 1: Remove Web Layer Legacy Components

#### Step 1: Remove Legacy Web Controllers ✅

1. **Verify Replacement Controllers** ✅
   - Ensure `UserAuthController.java` covers all functionality of `AuthenticationController.java`
   - Ensure `UserMgmtController.java` covers all functionality of `UserController.java`
   - Run `iam_test_integration_v3.sh` to verify functionality

2. **Remove Legacy Controllers** ✅
   - Remove `AuthenticationController.java`
   - Remove `UserController.java`
   - Run `iam_test_integration_v3.sh` to verify functionality

#### Step 2: Remove Legacy DTOs ✅

1. **Verify Replacement DTOs** ✅
   - Ensure all DTOs in `web.dto` package have equivalents in `infrastructure.controllers.dto`
   - Run `iam_test_integration_v3.sh` to verify functionality

2. **Remove Legacy DTOs** ✅
   - Remove all DTOs in `web.dto` package
   - Run `iam_test_integration_v3.sh` to verify functionality

#### Step 3: Remove Web Package ✅

1. **Remove Web Package** ✅
   - After all controllers and DTOs are removed, remove the entire `web` package
   - Run `iam_test_integration_v3.sh` to verify functionality

### Phase 2: Remove Infrastructure Layer Legacy Components

#### Revised Approach for Phase 2

After our initial attempt to remove the legacy repositories directly, we encountered compilation errors in several dependent components. This indicates that we need to take a more gradual approach to Phase 2 of the migration:

1. **Step 1: Update Adapter Classes** ✅
   - Modify `AuthenticationServiceAdapter` and `UserServiceAdapter` to use the new DTOs
   - Update method signatures to match the new DTOs
   - Ensure all functionality is preserved
   - Run integration tests to verify

2. **Step 2: Update Service Classes** ✅
   - Modify `AuthenticationService` and `UserService` to use the new DTOs
   - Update method signatures to match the new DTOs
   - Run integration tests to verify

3. **Step 3: Prepare Repository Adapters**
   - Ensure `IamRepositoryAdapter` and `UserRepositoryAdapter` properly delegate to the new repositories
   - Update any references to use the adapters instead of direct repository references
   - Run integration tests to verify

4. **Step 4: Create Interface Bridges**
   - Create interface bridges that extend both old and new repository interfaces
   - Update adapters to implement these bridge interfaces
   - Run integration tests to verify

5. **Step 5: Remove Legacy Repositories**
   - Remove legacy repositories
   - Run integration tests to verify

### Phase 3: Remove Application Layer Legacy Components

#### Step 1: Remove Security Components

1. **Verify Replacement Security Components**
   - Ensure `IamJwtAuthenticationFilter.java` covers all functionality of `JwtAuthenticationFilter.java`
   - Ensure `JwtService.java` in infrastructure.security covers all functionality of the application layer version
   - Ensure `IamSecurityConfig.java` covers all functionality of `SecurityConfiguration.java` and `SecurityConfigurationAdapter.java`
   - Run `iam_test_integration_v3.sh` to verify functionality

2. **Remove Legacy Security Components**
   - Remove `JwtAuthenticationFilter.java` from application layer
   - Remove `JwtService.java` from application layer
   - Remove `SecurityConfiguration.java`
   - Remove `SecurityConfigurationAdapter.java`
   - Run `iam_test_integration_v3.sh` to verify functionality

#### Step 2: Remove Legacy Services

1. **Verify Replacement Use Cases**
   - Ensure use cases cover all functionality of `AuthenticationService.java`
   - Ensure use cases cover all functionality of `UserService.java`
   - Ensure `AuthenticationServiceAdapter.java` and `UserServiceAdapter.java` are properly bridging between legacy and clean architecture
   - Run `iam_test_integration_v3.sh` to verify functionality

2. **Remove Legacy Services**
   - Remove `AuthenticationService.java`
   - Remove `UserService.java`
   - Run `iam_test_integration_v3.sh` to verify functionality

### Phase 4: Remove Domain Layer Legacy Components

#### Step 1: Remove Legacy Domain Entities

1. **Verify Replacement Domain Entities**
   - Ensure `User.java` covers all functionality of `UserEntity.java`
   - Ensure `UserDetailsAdapter.java` covers all functionality of `UserDetailsEntity.java`
   - Ensure `UserEntityAdapter.java` is properly bridging between legacy and clean architecture
   - Run `iam_test_integration_v3.sh` to verify functionality

2. **Remove Legacy Domain Entities**
   - Remove `UserEntity.java` from domain layer
   - Remove `UserDetailsEntity.java` from domain layer
   - Run `iam_test_integration_v3.sh` to verify functionality

### Phase 5: Remove Adapter Components

After all legacy components are removed, the adapter components can be removed:

#### Step 1: Remove Infrastructure Layer Adapters

1. **Update References to Infrastructure Adapters**
   - Update all references to `UserRepositoryAdapter.java` to use `UserRepositoryGateway.java` directly
   - Update all references to `IamRepositoryAdapter.java` to use `UserRepositoryGateway.java` directly
   - Run `iam_test_integration_v3.sh` to verify functionality

2. **Remove Infrastructure Adapters**
   - Remove `UserRepositoryAdapter.java`
   - Remove `IamRepositoryAdapter.java`
   - Keep `UserDetailsServiceAdapter.java` as it's part of the Spring Security integration
   - Run `iam_test_integration_v3.sh` to verify functionality

#### Step 2: Remove Application Layer Adapters

1. **Update References to Application Adapters**
   - Update all references to `AuthenticationServiceAdapter.java` to use use cases directly
   - Update all references to `UserServiceAdapter.java` to use use cases directly
   - Run `iam_test_integration_v3.sh` to verify functionality

2. **Remove Application Adapters**
   - Remove `AuthenticationServiceAdapter.java`
   - Remove `UserServiceAdapter.java`
   - Run `iam_test_integration_v3.sh` to verify functionality

#### Step 3: Remove Domain Layer Adapters

1. **Update References to Domain Adapters**
   - Update all references to `UserEntityAdapter.java` to use `User.java` directly
   - Run `iam_test_integration_v3.sh` to verify functionality

2. **Remove Domain Adapters**
   - Remove `UserEntityAdapter.java`
   - Run `iam_test_integration_v3.sh` to verify functionality

## Detailed Removal Process for Each Component

### Web Layer Components

#### `AuthenticationController.java` ✅
- **Dependencies**: 
  - `AuthenticationService.java`
  - Web DTOs
- **Replacement**: `UserAuthController.java` in infrastructure.controllers
- **Verification Steps**:
  1. Ensure all endpoints are implemented in `UserAuthController.java`
  2. Ensure all functionality is preserved
  3. Run `iam_test_integration_v3.sh` to verify

#### `UserController.java` ✅
- **Dependencies**:
  - `UserService.java`
  - Web DTOs
- **Replacement**: `UserMgmtController.java` in infrastructure.controllers
- **Verification Steps**:
  1. Ensure all endpoints are implemented in `UserMgmtController.java`
  2. Ensure all functionality is preserved
  3. Run `iam_test_integration_v3.sh` to verify

### Infrastructure Layer Components

#### `UserRepository.java` (in infrastructure root)
- **Dependencies**: Used by legacy services
- **Replacement**: `UserRepository.java` in infrastructure.persistence
- **Verification Steps**:
  1. Ensure all methods are implemented in the new location
  2. Ensure all functionality is preserved
  3. Run `iam_test_integration_v3.sh` to verify

#### `IamRepository.java`
- **Dependencies**: Used by legacy services
- **Replacement**: `UserGateway.java` interface and `UserRepositoryGateway.java` implementation
- **Adapter**: `IamRepositoryAdapter.java`
- **Verification Steps**:
  1. Ensure all methods are implemented in `UserRepositoryGateway.java`
  2. Ensure `IamRepositoryAdapter.java` properly bridges between legacy and clean architecture
  3. Run `iam_test_integration_v3.sh` to verify

### Application Layer Components

#### `JwtAuthenticationFilter.java` (application layer)
- **Dependencies**: Spring Security integration
- **Replacement**: `IamJwtAuthenticationFilter.java` in infrastructure.security
- **Verification Steps**:
  1. Ensure all functionality is implemented in `IamJwtAuthenticationFilter.java`
  2. Run `iam_test_integration_v3.sh` to verify

#### `JwtService.java` (application layer)
- **Dependencies**: JWT token handling
- **Replacement**: `JwtService.java` in infrastructure.security
- **Verification Steps**:
  1. Ensure all functionality is implemented in the new location
  2. Run `iam_test_integration_v3.sh` to verify

#### `SecurityConfiguration.java`
- **Dependencies**: Spring Security configuration
- **Replacement**: `IamSecurityConfig.java` in infrastructure.config
- **Verification Steps**:
  1. Ensure all configuration is implemented in `IamSecurityConfig.java`
  2. Run `iam_test_integration_v3.sh` to verify

#### `SecurityConfigurationAdapter.java`
- **Dependencies**: Spring Security configuration
- **Replacement**: `IamSecurityConfig.java` in infrastructure.config
- **Verification Steps**:
  1. Ensure all configuration is implemented in `IamSecurityConfig.java`
  2. Run `iam_test_integration_v3.sh` to verify

#### `AuthenticationService.java`
- **Dependencies**: Used by web controllers
- **Replacement**: `AuthenticateUserUseCase.java` and `RegisterUserUseCase.java`
- **Adapter**: `AuthenticationServiceAdapter.java`
- **Verification Steps**:
  1. Ensure all functionality is implemented in use cases
  2. Ensure `AuthenticationServiceAdapter.java` properly bridges between legacy and clean architecture
  3. Run `iam_test_integration_v3.sh` to verify

#### `UserService.java`
- **Dependencies**: Used by web controllers
- **Replacement**: Various use cases (`GetUserByCpfUseCase.java`, `GetAllUsersUseCase.java`, `DeleteUserUseCase.java`)
- **Adapter**: `UserServiceAdapter.java`
- **Verification Steps**:
  1. Ensure all functionality is implemented in use cases
  2. Ensure `UserServiceAdapter.java` properly bridges between legacy and clean architecture
  3. Run `iam_test_integration_v3.sh` to verify

### Domain Layer Components

#### `UserEntity.java` (domain layer)
- **Dependencies**: Used by legacy services
- **Replacement**: `User.java` in domain.entity
- **Adapter**: `UserEntityAdapter.java`
- **Verification Steps**:
  1. Ensure all fields and methods are implemented in `User.java`
  2. Ensure `UserEntityAdapter.java` properly bridges between legacy and clean architecture
  3. Run `iam_test_integration_v3.sh` to verify

#### `UserDetailsEntity.java` (domain layer)
- **Dependencies**: Used by Spring Security integration
- **Replacement**: `UserDetailsAdapter.java` in infrastructure.security
- **Verification Steps**:
  1. Ensure all functionality is implemented in `UserDetailsAdapter.java`
  2. Run `iam_test_integration_v3.sh` to verify

## Testing Strategy

After each removal step:

1. **Run Integration Tests**
   - Execute `iam_test_integration_v3.sh` to verify functionality
   - Fix any issues before proceeding to the next step

2. **Manual Testing**
   - Test key user flows:
     - User registration
     - User authentication
     - User management operations
   - Verify security features:
     - JWT token generation and validation
     - Role-based access control

3. **Compilation Checks**
   - Ensure the codebase compiles without errors
   - Address any warnings that might indicate issues

## Rollback Plan

In case of issues during migration:

1. **Keep Backup of Legacy Components**
   - Before removing any component, keep a backup
   - Document the removal steps for easy rollback

2. **Incremental Approach**
   - Remove one component at a time
   - Test thoroughly after each removal
   - Roll back if issues are found

3. **Feature Flags**
   - Consider using feature flags to switch between legacy and new implementations
   - This allows for easy rollback if issues are found

## Timeline and Prioritization

1. **Week 1: Web Layer Migration** ✅
   - Remove legacy controllers and DTOs
   - Remove web package

2. **Week 2: Infrastructure Layer Migration**
   - Remove legacy repositories
   - Update references to use clean architecture components

3. **Week 3: Application Layer Migration**
   - Remove legacy security components
   - Remove legacy services

4. **Week 4: Domain Layer Migration**
   - Remove legacy domain entities
   - Begin adapter removal

5. **Week 5: Adapter Removal**
   - Remove infrastructure adapters
   - Remove application adapters
   - Remove domain adapters

6. **Week 6: Final Testing and Documentation**
   - Comprehensive testing
   - Update documentation
   - Final code review

## Progress Tracking

### Completed Steps
- ✅ Updated controller mappings from `/api/v2/user/*` to `/api/user/*`
- ✅ Removed legacy web controllers (`AuthenticationController.java`, `UserController.java`)
- ✅ Removed legacy DTOs in web.dto package
- ✅ Removed entire web package
- ✅ Updated test script to use new endpoint paths
- ✅ Verified functionality with integration tests
- ✅ Updated adapter classes to use new DTOs from `infrastructure.controllers.dto`
- ✅ Updated service classes to use new DTOs
- ✅ Added explicit bean names to adapter components
- ✅ Added explicit qualifiers to repository components
- ✅ Added explicit bean names and qualifiers to security components
- ✅ Added explicit bean names and qualifiers to application service components
- ✅ Verified functionality with integration tests after application service component updates
- ✅ Completed comprehensive dependency audit

### Current Work
- Preparing for systematic removal of legacy components

### Next Steps
- Begin removing legacy components following the detailed removal plan

## Dependency Audit Results

### Legacy Domain Entities
- `UserEntity.java` is used by:
  - Application layer: `ApplicationConfiguration`, `AuthenticationService`, `UserService`
  - Adapters: `AuthenticationServiceAdapter`, `UserServiceAdapter`, `UserEntityAdapter`
  - Infrastructure: `IamRepository`, `UserRepository`, `IamRepositoryAdapter`, `UserRepositoryAdapter`

- `UserDetailsEntity.java` is used by:
  - Application layer: `ApplicationConfiguration`, `AuthenticationService`, `UserService`
  - Adapters: `AuthenticationServiceAdapter`, `UserEntityAdapter`
  - Infrastructure: `UserRepository`

### Legacy Infrastructure Components
- `IamRepository.java` is used by:
  - Application layer: `ApplicationConfiguration`, `AuthenticationService`, `UserService`
  - Adapters: `AuthenticationServiceAdapter`, `UserServiceAdapter`
  - Infrastructure: `IamRepositoryAdapter`

- `UserRepository.java` is used by:
  - Application layer: `UserService`
  - Adapters: `UserServiceAdapter`
  - Infrastructure: `UserRepositoryAdapter`

### Legacy Application Components
- `AuthenticationService.java` is only used by `AuthenticationServiceAdapter`
- `UserService.java` is only used by `UserServiceAdapter`
- `JwtService.java` is used by `JwtAuthenticationFilter` and referenced in `UserAuthController`

## Key Insights

1. **Clean Controllers**: The new controllers (`UserAuthController`, `UserMgmtController`) are already using the clean architecture components (use cases, domain entities) directly, not the legacy services.

2. **Adapter Pattern Working**: The adapter pattern is working as intended - legacy components are only referenced by their respective adapters or other legacy components.

3. **Dependency Direction**: Dependencies flow in the correct direction, with outer layers depending on inner layers, which will make removal easier.

4. **Isolated Legacy Components**: Most legacy components are well-isolated and only used by their adapters, making them good candidates for removal.

## Detailed Removal Plan

### Phase 1: Remove Legacy Application Components

1. **Remove JwtService.java**
   - Update `JwtAuthenticationFilter` to use the new `JwtService` directly
   - Ensure `UserAuthController` is using the new `JwtService` with proper qualifiers
   - Run `iam_test_integration_v3.sh` to verify functionality

2. **Remove AuthenticationService.java**
   - Update any remaining references to use `AuthenticationServiceAdapter` directly
   - Remove the class and update `AuthenticationServiceAdapter` to implement the necessary interfaces directly
   - Run `iam_test_integration_v3.sh` to verify functionality

3. **Remove UserService.java**
   - Update any remaining references to use `UserServiceAdapter` directly
   - Remove the class and update `UserServiceAdapter` to implement the necessary interfaces directly
   - Run `iam_test_integration_v3.sh` to verify functionality

### Phase 2: Remove Legacy Infrastructure Components

1. **Remove IamRepository.java**
   - Update `ApplicationConfiguration` to use the new repository interfaces
   - Update any remaining references to use `IamRepositoryAdapter` or the new repositories directly
   - Remove the interface
   - Run `iam_test_integration_v3.sh` to verify functionality

2. **Remove UserRepository.java**
   - Update any remaining references to use `UserRepositoryAdapter` or the new repositories directly
   - Remove the interface
   - Run `iam_test_integration_v3.sh` to verify functionality

### Phase 3: Remove Legacy Domain Entities

1. **Remove UserDetailsEntity.java**
   - Update `ApplicationConfiguration` to use the new domain entities
   - Update any remaining references to use `UserEntityAdapter` or the new domain entities directly
   - Remove the class
   - Run `iam_test_integration_v3.sh` to verify functionality

2. **Remove UserEntity.java**
   - Update any remaining references to use `UserEntityAdapter` or the new domain entities directly
   - Remove the class
   - Run `iam_test_integration_v3.sh` to verify functionality

### Phase 4: Remove Adapter Components

1. **Remove UserEntityAdapter.java**
   - Update any remaining references to use the new domain entities directly
   - Remove the class
   - Run `iam_test_integration_v3.sh` to verify functionality

2. **Remove IamRepositoryAdapter.java and UserRepositoryAdapter.java**
   - Update any remaining references to use the new repositories directly
   - Remove the classes
   - Run `iam_test_integration_v3.sh` to verify functionality

3. **Remove AuthenticationServiceAdapter.java and UserServiceAdapter.java**
   - Update any remaining references to use the use cases directly
   - Remove the classes
   - Run `iam_test_integration_v3.sh` to verify functionality

## Migration Strategy

The migration will follow the natural dependency direction (from outer layers to inner layers) while leveraging the adapter pattern to maintain functionality throughout the process:

1. **Start with Web Layer**: ✅ Remove legacy controllers and DTOs (COMPLETED)
2. **Application Layer**: Remove legacy services and security components
3. **Infrastructure Layer**: Remove legacy repositories and infrastructure components
4. **Domain Layer**: Remove legacy domain entities
5. **Remove Adapters**: Once all legacy components are removed, remove the adapter components

After each step, we will run `iam_test_integration_v3.sh` to verify that everything still works correctly.

## Detailed Migration Steps

### Phase 1: Remove Web Layer Legacy Components ✅

#### Step 1: Remove Legacy Web Controllers ✅

1. **Verify Replacement Controllers** ✅
   - Ensure `UserAuthController.java` covers all functionality of `AuthenticationController.java`
   - Ensure `UserMgmtController.java` covers all functionality of `UserController.java`
   - Run `iam_test_integration_v3.sh` to verify functionality

2. **Remove Legacy Controllers** ✅
   - Remove `AuthenticationController.java`
   - Remove `UserController.java`
   - Run `iam_test_integration_v3.sh` to verify functionality

#### Step 2: Remove Legacy DTOs ✅

1. **Verify Replacement DTOs** ✅
   - Ensure all DTOs in `web.dto` package have equivalents in `infrastructure.controllers.dto`
   - Run `iam_test_integration_v3.sh` to verify functionality

2. **Remove Legacy DTOs** ✅
   - Remove all DTOs in `web.dto` package
   - Run `iam_test_integration_v3.sh` to verify functionality

#### Step 3: Remove Web Package ✅

1. **Remove Web Package** ✅
   - After all controllers and DTOs are removed, remove the entire `web` package
   - Run `iam_test_integration_v3.sh` to verify functionality

### Phase 2: Remove Application Layer Legacy Components

#### Step 1: Remove JwtService.java

1. **Update JwtAuthenticationFilter**
   - Modify `JwtAuthenticationFilter` to use the new `JwtService` directly
   - Ensure proper qualifiers are used
   - Run `iam_test_integration_v3.sh` to verify functionality

2. **Remove Legacy JwtService**
   - Remove `JwtService.java` from the application layer
   - Run `iam_test_integration_v3.sh` to verify functionality

#### Step 2: Remove AuthenticationService.java

1. **Update References**
   - Identify any remaining references to `AuthenticationService`
   - Update them to use `AuthenticationServiceAdapter` directly
   - Run `iam_test_integration_v3.sh` to verify functionality

2. **Remove Legacy Service**
   - Remove `AuthenticationService.java`
   - Update `AuthenticationServiceAdapter` to implement necessary interfaces directly
   - Run `iam_test_integration_v3.sh` to verify functionality

#### Step 3: Remove UserService.java

1. **Update References**
   - Identify any remaining references to `UserService`
   - Update them to use `UserServiceAdapter` directly
   - Run `iam_test_integration_v3.sh` to verify functionality

2. **Remove Legacy Service**
   - Remove `UserService.java`
   - Update `UserServiceAdapter` to implement necessary interfaces directly
   - Run `iam_test_integration_v3.sh` to verify functionality

### Phase 3: Remove Infrastructure Layer Legacy Components

#### Step 1: Remove IamRepository.java

1. **Update ApplicationConfiguration**
   - Modify `ApplicationConfiguration` to use the new repository interfaces
   - Run `iam_test_integration_v3.sh` to verify functionality

2. **Update References**
   - Identify any remaining references to `IamRepository`
   - Update them to use `IamRepositoryAdapter` or the new repositories directly
   - Run `iam_test_integration_v3.sh` to verify functionality

3. **Remove Legacy Repository**
   - Remove `IamRepository.java`
   - Run `iam_test_integration_v3.sh` to verify functionality

#### Step 2: Remove UserRepository.java

1. **Update References**
   - Identify any remaining references to `UserRepository`
   - Update them to use `UserRepositoryAdapter` or the new repositories directly
   - Run `iam_test_integration_v3.sh` to verify functionality

2. **Remove Legacy Repository**
   - Remove `UserRepository.java`
   - Run `iam_test_integration_v3.sh` to verify functionality

### Phase 4: Remove Domain Layer Legacy Components

#### Step 1: Remove UserDetailsEntity.java

1. **Update ApplicationConfiguration**
   - Modify `ApplicationConfiguration` to use the new domain entities
   - Run `iam_test_integration_v3.sh` to verify functionality

2. **Update References**
   - Identify any remaining references to `UserDetailsEntity`
   - Update them to use `UserEntityAdapter` or the new domain entities directly
   - Run `iam_test_integration_v3.sh` to verify functionality

3. **Remove Legacy Entity**
   - Remove `UserDetailsEntity.java`
   - Run `iam_test_integration_v3.sh` to verify functionality

#### Step 2: Remove UserEntity.java

1. **Update References**
   - Identify any remaining references to `UserEntity`
   - Update them to use `UserEntityAdapter` or the new domain entities directly
   - Run `iam_test_integration_v3.sh` to verify functionality

2. **Remove Legacy Entity**
   - Remove `UserEntity.java`
   - Run `iam_test_integration_v3.sh` to verify functionality

### Phase 5: Remove Adapter Components

#### Step 1: Remove Domain Layer Adapters

1. **Update References to UserEntityAdapter**
   - Identify any remaining references to `UserEntityAdapter`
   - Update them to use the new domain entities directly
   - Run `iam_test_integration_v3.sh` to verify functionality

2. **Remove Domain Adapters**
   - Remove `UserEntityAdapter.java`
   - Run `iam_test_integration_v3.sh` to verify functionality

#### Step 2: Remove Infrastructure Layer Adapters

1. **Update References to Repository Adapters**
   - Update all references to `IamRepositoryAdapter` and `UserRepositoryAdapter` to use the new repositories directly
   - Run `iam_test_integration_v3.sh` to verify functionality

2. **Remove Infrastructure Adapters**
   - Remove `IamRepositoryAdapter.java`
   - Remove `UserRepositoryAdapter.java`
   - Run `iam_test_integration_v3.sh` to verify functionality

#### Step 3: Remove Application Layer Adapters

1. **Update References to Service Adapters**
   - Update all references to `AuthenticationServiceAdapter` and `UserServiceAdapter` to use the use cases directly
   - Run `iam_test_integration_v3.sh` to verify functionality

2. **Remove Application Adapters**
   - Remove `AuthenticationServiceAdapter.java`
   - Remove `UserServiceAdapter.java`
   - Run `iam_test_integration_v3.sh` to verify functionality

## Conclusion

This migration plan provides a detailed roadmap for removing legacy code from the IAM module and completing the migration to a clean architecture structure. By following the natural dependency direction (from outer layers to inner layers) while leveraging the adapter pattern, we can maintain functionality throughout the migration process and minimize the risk of breaking changes.

The plan acknowledges that many clean architecture components are already in place and focuses on removing legacy components and adapters in a systematic way. After each step, we will run `iam_test_integration_v3.sh` to verify that everything still works correctly, ensuring a smooth migration process.
