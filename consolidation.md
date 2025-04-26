# Configuration Consolidation Plan

This document outlines the remaining steps to achieve a fully consolidated, environment-independent configuration for the Snackbar Management application.

## Completed Improvements

1. **Spring Configuration Consolidation**
   - Removed Spring profiles
   - Consolidated application properties into a single file
   - Made all configuration driven by environment variables

2. **Docker Compose Consolidation**
   - Merged multiple Docker Compose files into a single file
   - Removed environment-specific configurations
   - Ensured all settings are driven by environment variables

3. **Dependency Management Cleanup** ✅
   - Fixed duplicate dependency declarations in pom.xml
   - Standardized version management using properties
   - Removed unused dependencies
   - Addressed Maven warnings about duplicate declarations

4. **Java Code Configuration Improvements** ✅
   - Updated `SQSProductMessageConsumer.java` to use environment variables for polling settings
   - Removed hardcoded values in Java code
   - Used `@Value` annotations consistently for all configurable values
   - Ensured all configuration is externalized via environment variables

## Remaining Improvements

### 1. Environment Variables Documentation

- **Create a `.env.example` file** with comprehensive documentation
  ```bash
  # Database Configuration
  DB_HOST=                      # MongoDB host (e.g., mongodb, localhost)
  DB_PORT=                      # MongoDB port (e.g., 27017)
  APP_DB=                       # Application database name
  MONGODB_USER=                 # MongoDB username
  MONGODB_PASSWORD=             # MongoDB password
  MONGODB_URI=                  # Full MongoDB connection URI
  ```

- **Document all required variables** with clear descriptions
- **Group variables logically** by their purpose (DB, AWS, logging, etc.)
- **Remove any sensitive values** from the example file

### 2. Cleanup of Compiled Classes in Git

- **Update `.gitignore`** to exclude `target/` directories
  ```
  # Maven
  target/
  *.class
  
  # IDE files
  .idea/
  .vscode/
  *.iml
  
  # Logs
  *.log
  
  # Local environment files
  .env.local
  .env.development.local
  ```

- **Remove already tracked `.class` files** from Git
  ```bash
  git rm --cached -r backend/target/
  ```

- **Add patterns for IDE-specific files** (.idea/, .vscode/, etc.)
- **Exclude any generated files** from version control

### 3. Consistent Naming Convention

- **Standardize environment variable naming** (e.g., AWS_*, DB_*, LOG_*)
- **Use consistent casing** (uppercase with underscores)
- **Group related variables** with common prefixes
- **Rename any inconsistently named variables**

Example of consistent naming:
```
# Database variables
DB_HOST
DB_PORT
DB_NAME

# AWS variables
AWS_REGION
AWS_SQS_QUEUE_URL
AWS_ACCESS_KEY

# Logging variables
LOG_LEVEL_ROOT
LOG_LEVEL_APP
```

### 4. Documentation Updates

- **Update README.md** with setup instructions
- **Document environment variables** and their purpose
- **Provide examples** for different deployment scenarios
- **Include troubleshooting information**

## Implementation Details

### Dependency Management Cleanup (Completed)

1. **Removed Unused Dependencies**:
   - mybatis-spring
   - spring-boot-starter-webflux
   - spring-boot-starter-actuator
   - javax.validation:validation-api
   - javax.servlet:javax.servlet-api
   - gson

2. **Standardized Version Management**:
   - Added version properties for consistent dependency management:
   ```xml
   <properties>
       <java.version>21</java.version>
       <aws.sdk.version>2.31.25</aws.sdk.version>
       <jwt.version>0.11.5</jwt.version>
       <lombok.version>1.18.34</lombok.version>
       <springdoc.version>2.6.0</springdoc.version>
       <spring.cloud.version>4.2.0</spring.cloud.version>
       <spring.security.version>5.8.4</spring.security.version>
   </properties>
   ```

3. **Organized Dependencies**:
   - Grouped related dependencies together with clear comments
   - Removed duplicate declarations

### Java Code Configuration Improvements (Completed)

1. **Updated SQSProductMessageConsumer.java**:
   - Replaced hardcoded `fixedRate = 10000` with `fixedRateString = "${aws.sqs.polling-delay-ms}"`
   - Added configuration properties using `@Value` annotations:
     - `aws.sqs.polling-enabled` - Controls whether polling is active
     - `aws.sqs.polling-delay-ms` - Controls the polling interval
     - `aws.sqs.max-messages` - Controls the maximum number of messages to retrieve
     - `aws.sqs.wait-time-seconds` - Controls the SQS long polling wait time

2. **Updated Environment Variables**:
   - Added `AWS_SQS_WAIT_TIME_SECONDS=5` to the `.env` file
   - Added the variable to docker-compose.yml
   - Added the property to application.properties

3. **Additional Configuration Improvements Identified**:
   - Created documentation for IAM module configuration improvements
   - Created documentation for Product domain configuration improvements
   - These additional improvements will be implemented in separate tasks

## Implementation Priority for Remaining Tasks

1. Cleanup of Compiled Classes in Git
2. Consistent Naming Convention
3. Environment Variables Documentation
4. Documentation Updates

## Benefits

- **Simplified Configuration**: One approach for all environments
- **Explicit Settings**: No hidden defaults or environment-specific behavior
- **Improved Maintainability**: Easier to understand and modify
- **Better Onboarding**: Clear documentation for new team members
- **Reduced Errors**: Consistent naming and structure prevents mistakes
