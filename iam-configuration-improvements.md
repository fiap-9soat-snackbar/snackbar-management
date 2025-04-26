# IAM Module Configuration Improvements

This document outlines the configuration improvements needed in the IAM module to align with the consolidation plan. These changes should be implemented in a separate task after completing the current SQS configuration improvements.

## Identified Hardcoded Values

### 1. OpenApiConfig.java

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

### 2. JwtService.java

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

## Additional Considerations

1. **Security Configuration**
   - Review any hardcoded security settings in `SecurityConfiguration.java`
   - Ensure CORS settings are configurable via environment variables

2. **User Service**
   - Check for any hardcoded user roles or permissions
   - Ensure password policy settings are configurable

3. **Authentication Controller**
   - Review for any hardcoded authentication settings like token refresh times

## Implementation Priority

These IAM configuration improvements should be implemented after completing the current SQS configuration task. They are important but less critical than the SQS polling settings that directly affect system performance and resource usage.

## Benefits

- Improved security through externalized configuration
- Consistent configuration approach across all modules
- Better maintainability and easier deployment to different environments
- Alignment with the overall consolidation plan
