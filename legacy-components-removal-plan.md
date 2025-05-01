# IAM Legacy Components Removal Plan

This document outlines the plan for removing legacy components from the IAM module, starting with components that have the fewest dependencies.

## Progress Tracking

### Completed
- ✅ Moved IamServiceImpl.java to junkyard folder
- ✅ Moved IamService.java to junkyard folder
- ✅ Moved ApplicationConfiguration.java to junkyard folder
- ✅ Moved OpenApiConfig.java to junkyard folder
- ✅ Created adapter for JwtAuthenticationFilter.java that delegates to IamJwtAuthenticationFilter
- ✅ Created SecurityConfigurationAdapter that delegates to IamSecurityConfig
- ✅ Created UserEntityAdapter for converting between legacy entities and new domain entities
- ✅ Marked UserDetailsEntity and UserEntity as @Deprecated

### In Progress
- 🔄 Preparing for high-impact component removal

### Pending
- ⏳ High-impact component removal
- ⏳ Final cleanup

## Components with Fewest Dependencies (Phase 1) - COMPLETED

These components have been successfully moved to the junkyard folder:

1. **IamServiceImpl.java** ✅
   - Located in: `com.snackbar.iam.application`
   - Dependencies: IamService interface
   - Used by: None (appears to be unused)
   - Removal impact: Low - No direct usage identified
   - **Status**: Moved to junkyard

2. **IamService.java** ✅
   - Located in: `com.snackbar.iam.application`
   - Dependencies: None
   - Used by: IamServiceImpl
   - Removal impact: Low - No direct usage in controllers or other services
   - **Status**: Moved to junkyard

3. **ApplicationConfiguration.java** ✅
   - Located in: `com.snackbar.iam.application`
   - Dependencies: None
   - Used by: Spring context
   - Removal impact: Low - Can be replaced by new configuration classes
   - **Status**: Moved to junkyard

4. **OpenApiConfig.java** ✅
   - Located in: `com.snackbar.iam.application`
   - Dependencies: None
   - Used by: Swagger/OpenAPI documentation
   - Removal impact: Low - Can be replaced by a new configuration in the infrastructure layer
   - **Status**: Moved to junkyard

5. **JwtAuthenticationFilter.java** ✅
   - Located in: `com.snackbar.iam.application`
   - Dependencies: JwtService, UserDetailsService
   - Used by: SecurityConfiguration
   - Removal impact: Medium - Needs to be fully replaced by IamJwtAuthenticationFilter
   - **Status**: Created adapter that delegates to IamJwtAuthenticationFilter

## Components with Medium Dependencies (Phase 2) - COMPLETED

These components have been successfully adapted to work with the new clean architecture components:

1. **SecurityConfiguration.java** ✅
   - Located in: `com.snackbar.iam.application`
   - Dependencies: JwtAuthenticationFilter
   - Used by: Spring Security context
   - Removal impact: Medium - Needs to be fully replaced by IamSecurityConfig
   - **Status**: 
     - Created SecurityConfigurationAdapter that delegates to IamSecurityConfig
     - Used securityMatcher to avoid filter chain conflicts
     - Marked original SecurityConfiguration as @Deprecated
     - Added proper bean naming to avoid conflicts

2. **JwtService.java** ✅
   - Located in: `com.snackbar.iam.application`
   - Dependencies: None (but delegates to new JwtService)
   - Used by: AuthenticationController, UserController
   - Removal impact: Medium - Controllers need to be updated to use the new JwtService
   - **Status**:
     - Ensured legacy JwtService properly delegates to new implementation
     - Added @Qualifier annotations to distinguish services
     - Marked as @Deprecated

3. **UserDetailsEntity.java** ✅
   - Located in: `com.snackbar.iam.domain`
   - Dependencies: None
   - Used by: AuthenticationService, UserService
   - Removal impact: Medium - Services need to be updated to use the new User entity
   - **Status**:
     - Created UserEntityAdapter for conversion between entities
     - Marked as @Deprecated
     - Added proper documentation

4. **UserEntity.java** ✅
   - Located in: `com.snackbar.iam.domain`
   - Dependencies: None
   - Used by: IamRepository, UserRepository, AuthenticationService, UserService
   - Removal impact: Medium - Services and repositories need to be updated to use the new User entity
   - **Status**:
     - Created UserEntityAdapter for conversion between entities
     - Marked as @Deprecated
     - Added proper documentation

## Components with High Dependencies (Phase 3) - NEXT STEPS

These components have the most dependencies and should be removed next. Detailed implementation steps are provided:

1. **IamRepository.java**
   - Located in: `com.snackbar.iam.infrastructure`
   - Dependencies: UserEntity
   - Used by: AuthenticationService, UserService
   - Removal impact: High - Services need to be updated to use the new UserRepository
   - **Implementation Steps**:
     1. Create an adapter class that delegates to the new UserRepository
     2. Update the adapter to convert between entity types using UserEntityAdapter
     3. Mark the original repository as @Deprecated
     4. Add unit tests to verify the adapter works correctly

2. **UserRepository.java** (legacy)
   - Located in: `com.snackbar.iam.infrastructure`
   - Dependencies: UserEntity
   - Used by: UserService
   - Removal impact: High - UserService needs to be updated to use the new UserRepository
   - **Implementation Steps**:
     1. Create an adapter class that delegates to the new UserRepository
     2. Update the adapter to convert between entity types using UserEntityAdapter
     3. Mark the original repository as @Deprecated
     4. Add unit tests to verify the adapter works correctly

3. **AuthenticationService.java**
   - Located in: `com.snackbar.iam.application`
   - Dependencies: IamRepository, PasswordEncoder, AuthenticationManager
   - Used by: AuthenticationController, UserController
   - Removal impact: High - Controllers need to be updated to use the new use cases
   - **Implementation Steps**:
     1. Create an adapter class that delegates to the new authentication use cases
     2. Update the adapter to convert between entity types using UserEntityAdapter
     3. Mark the original service as @Deprecated
     4. Add unit tests to verify the adapter works correctly

4. **UserService.java**
   - Located in: `com.snackbar.iam.application`
   - Dependencies: IamRepository, UserRepository
   - Used by: AuthenticationController, UserController
   - Removal impact: High - Controllers need to be updated to use the new use cases
   - **Implementation Steps**:
     1. Create an adapter class that delegates to the new user use cases
     2. Update the adapter to convert between entity types using UserEntityAdapter
     3. Mark the original service as @Deprecated
     4. Add unit tests to verify the adapter works correctly

5. **AuthenticationController.java**
   - Located in: `com.snackbar.iam.web`
   - Dependencies: JwtService, AuthenticationService, UserService
   - Used by: API clients
   - Removal impact: High - API clients need to migrate to the new endpoints
   - **Implementation Steps**:
     1. Create redirects from old endpoints to new endpoints
     2. Update the controller to use the new use cases through adapters
     3. Mark the original controller as @Deprecated
     4. Add integration tests to verify the redirects work correctly

6. **UserController.java**
   - Located in: `com.snackbar.iam.web`
   - Dependencies: JwtService, AuthenticationService, UserService
   - Used by: API clients
   - Removal impact: High - API clients need to migrate to the new endpoints
   - **Implementation Steps**:
     1. Create redirects from old endpoints to new endpoints
     2. Update the controller to use the new use cases through adapters
     3. Mark the original controller as @Deprecated
     4. Add integration tests to verify the redirects work correctly

## Removal Strategy

1. **Phase 1: Remove Low-Impact Components** ✅
   - Move IamServiceImpl.java to junkyard folder ✅
   - Move IamService.java to junkyard folder ✅
   - Move ApplicationConfiguration.java to junkyard folder ✅
   - Move OpenApiConfig.java to junkyard folder ✅
   - Create adapter for JwtAuthenticationFilter to use IamJwtAuthenticationFilter ✅

2. **Phase 2: Remove Medium-Impact Components** ✅
   - Create adapter for SecurityConfiguration to use IamSecurityConfig ✅
   - Ensure JwtService properly delegates to new implementation ✅
   - Create adapters for UserDetailsEntity to User entity ✅
   - Create adapters for UserEntity to User entity ✅

3. **Phase 3: Remove High-Impact Components**
   - Create adapters for IamRepository to use new UserRepository
   - Create adapters for legacy UserRepository to use new UserRepository
   - Implement new use cases to replace AuthenticationService and UserService
   - Create redirects from old API endpoints to new endpoints

4. **Phase 4: Final Cleanup**
   - Remove all redirects and adapters
   - Remove all legacy components
   - Update documentation

## Testing Strategy

Before removing each component:
1. Create unit tests for the replacement component
2. Create integration tests that verify the functionality
3. Run all existing tests to ensure no regressions

After removing each component:
1. Run all tests again to verify functionality
2. Manually test affected features
3. Update documentation to reflect the changes

## Lessons Learned from Phase 1 & 2

1. **Adapter Pattern Works Well**: The adapter pattern allowed us to maintain backward compatibility while transitioning to new implementations.
2. **Integration Testing is Critical**: Running integration tests after each component removal helped verify that the system still works correctly.
3. **Dependency Management**: Understanding the dependencies between components is crucial for a successful migration.
4. **Incremental Approach**: Moving components one by one and testing after each change proved to be a safe approach.
5. **Security Configuration Requires Special Care**: When dealing with security configurations, it's important to use proper security matchers and bean naming to avoid conflicts.
6. **Entity Conversion**: Using adapter classes for entity conversion helps maintain a clean separation between legacy and new components.
7. **Proper Documentation**: Adding @Deprecated annotations and clear documentation helps other developers understand the migration path.
