# Dependency Analysis for Snackbar Management

This document analyzes the dependencies in the Snackbar Management project, identifying which ones are actively used in the codebase and which ones could potentially be removed.

## Dependencies in Use

| Dependency | Usage in Codebase | Required By |
|------------|------------------|-------------|
| spring-boot-starter-data-mongodb | MongoDB repositories and entities | Product and User entities/repositories |
| spring-boot-starter-web | REST controllers and web infrastructure | All controllers |
| spring-boot-starter-security | Authentication and authorization | JWT authentication filter and security config |
| spring-boot-starter-validation | Bean validation | Entity validation |
| lombok | Reduces boilerplate code | User entities and DTOs |
| jjwt-api, jjwt-impl, jjwt-jackson | JWT token handling | JwtService |
| spring-security-oauth2-jose | JWT token validation | Security configuration |
| spring-cloud-starter-openfeign | REST client | FeignClientConfig |
| springdoc-openapi-starter-webmvc-ui | API documentation | OpenApiConfig |
| aws-sdk-sqs | SQS messaging | SQS message consumers/producers |
| aws-sdk-apache-client | HTTP client for AWS | Required by SQS client |
| jackson-datatype-jsr310 | JSON serialization of Java 8 date/time | Used with SQS message serialization |

## Unused or Potentially Removable Dependencies

| Dependency | Status | Notes |
|------------|--------|-------|
| mybatis-spring | **Unused** | No imports found in codebase |
| spring-boot-starter-webflux | **Unused** | No reactive programming imports found |
| spring-boot-starter-actuator | **Unused** | No actuator endpoints configured |
| javax.validation:validation-api | **Redundant** | Spring Boot 3.x uses Jakarta EE validation |
| javax.servlet:javax.servlet-api | **Redundant** | Spring Boot 3.x uses Jakarta EE servlet API |
| gson | **Unused** | Explicitly excluded from spring-boot-starter-web but added separately |

## Transitive Dependencies

Several dependencies are pulled in transitively by other dependencies:

1. **spring-boot-starter-web** brings in:
   - JSON processing (Jackson)
   - Tomcat embedded server
   - Spring MVC

2. **spring-boot-starter-security** brings in:
   - Spring Security Core
   - Spring Security Web
   - Spring Security Config

3. **aws-sdk-sqs** brings in:
   - AWS SDK Core
   - HTTP client components
   - JSON utilities

## Recommendations

1. **Remove Unused Dependencies**:
   - mybatis-spring
   - spring-boot-starter-webflux
   - spring-boot-starter-actuator
   - javax.validation:validation-api
   - javax.servlet:javax.servlet-api
   - gson

2. **Standardize Version Management**:
   - Continue using properties for version management
   - Ensure all AWS SDK components use the same version
   - Ensure all Spring components use compatible versions

3. **Dependency Conflicts**:
   - No major conflicts detected
   - Spring Security OAuth2 Jose (5.8.4) is older than the Spring Security Core (6.4.2) - consider upgrading

4. **Dependency Organization**:
   - Group related dependencies together in the POM
   - Add comments to clarify the purpose of each dependency group
