# Snackbar Application Troubleshooting

## Issue Identified
The application was failing to start due to a missing bean configuration for the IAM repository. The error message was:

```
Error creating bean with name 'jwtAuthenticationFilter': Unsatisfied dependency expressed through constructor parameter 1: 
Error creating bean with name 'applicationConfiguration': Unsatisfied dependency expressed through constructor parameter 0: 
No qualifying bean of type 'com.snackbar.iam.infrastructure.IamRepository' available
```

## Solution
1. Created a new configuration class for the IAM module to properly register MongoDB repositories:

```java
@Configuration
@EnableMongoRepositories(basePackages = "com.snackbar.iam.infrastructure")
public class IamConfig {
    // Configuration details
}
```

2. Added proper logging to help diagnose startup issues

3. Ensured proper cleanup between test runs:
   - `docker compose down -v --rmi all` to remove all containers, volumes and images
   - `rm -rf backend/target` to clean build artifacts
   - `mvn -f backend/pom.xml clean package` to rebuild the application
   - `docker compose up --build -d` to rebuild and start containers

## Best Practices
- Always use the `--build` flag with docker compose up to ensure fresh images
- Clean the target directory between builds to avoid stale artifacts
