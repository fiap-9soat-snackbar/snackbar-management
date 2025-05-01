# IAM Legacy Components Removal Plan

This document outlines the plan for removing legacy components from the IAM module, starting with components that have the fewest dependencies.

## Components with Fewest Dependencies

These components can be removed first as they have minimal dependencies:

1. **IamServiceImpl.java**
   - Located in: `com.snackbar.iam.application`
   - Dependencies: IamService interface
   - Used by: None (appears to be unused)
   - Removal impact: Low - No direct usage identified

2. **IamService.java**
   - Located in: `com.snackbar.iam.application`
   - Dependencies: None
   - Used by: IamServiceImpl
   - Removal impact: Low - No direct usage in controllers or other services

3. **ApplicationConfiguration.java**
   - Located in: `com.snackbar.iam.application`
   - Dependencies: None
   - Used by: Spring context
   - Removal impact: Low - Can be replaced by new configuration classes

4. **OpenApiConfig.java**
   - Located in: `com.snackbar.iam.application`
   - Dependencies: None
   - Used by: Swagger/OpenAPI documentation
   - Removal impact: Low - Can be replaced by a new configuration in the infrastructure layer

5. **JwtAuthenticationFilter.java**
   - Located in: `com.snackbar.iam.application`
   - Dependencies: JwtService, UserDetailsService
   - Used by: SecurityConfiguration
   - Removal impact: Medium - Needs to be fully replaced by IamJwtAuthenticationFilter

## Components with Medium Dependencies

These components have more dependencies but can be removed after the first group:

1. **SecurityConfiguration.java**
   - Located in: `com.snackbar.iam.application`
   - Dependencies: JwtAuthenticationFilter
   - Used by: Spring Security context
   - Removal impact: Medium - Needs to be fully replaced by IamSecurityConfig

2. **JwtService.java**
   - Located in: `com.snackbar.iam.application`
   - Dependencies: None (but delegates to new JwtService)
   - Used by: AuthenticationController, UserController
   - Removal impact: Medium - Controllers need to be updated to use the new JwtService

3. **UserDetailsEntity.java**
   - Located in: `com.snackbar.iam.domain`
   - Dependencies: None
   - Used by: AuthenticationService, UserService
   - Removal impact: Medium - Services need to be updated to use the new User entity

4. **UserEntity.java**
   - Located in: `com.snackbar.iam.domain`
   - Dependencies: None
   - Used by: IamRepository, UserRepository, AuthenticationService, UserService
   - Removal impact: Medium - Services and repositories need to be updated to use the new User entity

## Components with High Dependencies

These components have the most dependencies and should be removed last:

1. **IamRepository.java**
   - Located in: `com.snackbar.iam.infrastructure`
   - Dependencies: UserEntity
   - Used by: AuthenticationService, UserService
   - Removal impact: High - Services need to be updated to use the new UserRepository

2. **UserRepository.java** (legacy)
   - Located in: `com.snackbar.iam.infrastructure`
   - Dependencies: UserEntity
   - Used by: UserService
   - Removal impact: High - UserService needs to be updated to use the new UserRepository

3. **AuthenticationService.java**
   - Located in: `com.snackbar.iam.application`
   - Dependencies: IamRepository, PasswordEncoder, AuthenticationManager
   - Used by: AuthenticationController, UserController
   - Removal impact: High - Controllers need to be updated to use the new use cases

4. **UserService.java**
   - Located in: `com.snackbar.iam.application`
   - Dependencies: IamRepository, UserRepository
   - Used by: AuthenticationController, UserController
   - Removal impact: High - Controllers need to be updated to use the new use cases

5. **AuthenticationController.java**
   - Located in: `com.snackbar.iam.web`
   - Dependencies: JwtService, AuthenticationService, UserService
   - Used by: API clients
   - Removal impact: High - API clients need to migrate to the new endpoints

6. **UserController.java**
   - Located in: `com.snackbar.iam.web`
   - Dependencies: JwtService, AuthenticationService, UserService
   - Used by: API clients
   - Removal impact: High - API clients need to migrate to the new endpoints

## Removal Strategy

1. **Phase 1: Remove Low-Impact Components**
   - Move IamServiceImpl.java to legacy folder
   - Move IamService.java to legacy folder
   - Move ApplicationConfiguration.java to legacy folder
   - Move OpenApiConfig.java to legacy folder
   - Create redirects for JwtAuthenticationFilter to use IamJwtAuthenticationFilter

2. **Phase 2: Remove Medium-Impact Components**
   - Create redirects for SecurityConfiguration to use IamSecurityConfig
   - Update controllers to use new JwtService directly
   - Create adapters for UserDetailsEntity to User entity
   - Create adapters for UserEntity to User entity

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
