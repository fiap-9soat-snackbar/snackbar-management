## Best Practices
- Ensure proper cleanup between test runs:
   - `docker compose down -v --rmi all` to remove all containers, volumes and images
   - `rm -rf backend/target` to clean build artifacts
   - `mvn -f backend/pom.xml clean package` to rebuild the application
   - `docker compose up --build -d` to rebuild and start containers