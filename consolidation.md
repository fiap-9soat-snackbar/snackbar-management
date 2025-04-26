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

### 2. Java Code Configuration Improvements

- **Update `SQSProductMessageConsumer.java`** to use environment variables for polling settings
  ```java
  @Value("${aws.sqs.polling-enabled}")
  private boolean pollingEnabled;
  
  @Value("${aws.sqs.polling-delay-ms}")
  private long pollingDelayMs;
  
  @Value("${aws.sqs.max-messages}")
  private int maxMessages;
  
  @Scheduled(fixedRateString = "${aws.sqs.polling-delay-ms}")
  public void pollMessages() {
      if (!pollingEnabled) {
          logger.debug("SQS polling is disabled");
          return;
      }
      // ...
  }
  ```

- **Remove any remaining hardcoded values** in Java code
- **Use `@Value` annotations consistently** for all configurable values
- **Ensure all configuration is externalized** via environment variables

### 3. Cleanup of Compiled Classes in Git

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

### 4. Consistent Naming Convention

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

### 5. Dependency Management Cleanup

- **Fix duplicate dependency declarations** in pom.xml
  ```xml
  <!-- Current issue -->
  <dependency>
      <groupId>software.amazon.awssdk</groupId>
      <artifactId>sqs</artifactId>
      <version>2.31.25</version>
  </dependency>
  <!-- Later in the file -->
  <dependency>
      <groupId>software.amazon.awssdk</groupId>
      <artifactId>sqs</artifactId>
      <version>2.31.25</version>
  </dependency>
  ```

- **Standardize version management** using properties
  ```xml
  <properties>
      <aws.sdk.version>2.31.25</aws.sdk.version>
      <jackson.version>2.15.2</jackson.version>
  </properties>
  
  <dependencies>
      <dependency>
          <groupId>software.amazon.awssdk</groupId>
          <artifactId>sqs</artifactId>
          <version>${aws.sdk.version}</version>
      </dependency>
  </dependencies>
  ```

- **Remove unused dependencies** if any exist
- **Address Maven warnings** about duplicate declarations

### 6. Documentation Updates

- **Update README.md** with setup instructions
- **Document environment variables** and their purpose
- **Provide examples** for different deployment scenarios
- **Include troubleshooting information**

## Implementation Priority

1. Dependency Management Cleanup (highest priority - affects build stability)
2. Cleanup of Compiled Classes in Git
3. Java Code Configuration Improvements
4. Consistent Naming Convention
5. Environment Variables Documentation
6. Documentation Updates

## Benefits

- **Simplified Configuration**: One approach for all environments
- **Explicit Settings**: No hidden defaults or environment-specific behavior
- **Improved Maintainability**: Easier to understand and modify
- **Better Onboarding**: Clear documentation for new team members
- **Reduced Errors**: Consistent naming and structure prevents mistakes
