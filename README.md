# Snackbar Management System

<p align="center">
	<img alt="Spring boot" src="https://img.shields.io/badge/Spring%20Boot-6DB33F.svg?style=for-the-badge&logo=Spring-Boot&logoColor=white">
	<img alt="Maven" src="https://img.shields.io/badge/Apache%20Maven-C71A36.svg?style=for-the-badge&logo=Apache-Maven&logoColor=white">
	<img alt="MongoDb" src="https://img.shields.io/badge/MongoDB-47A248.svg?style=for-the-badge&logo=MongoDB&logoColor=white">
	<img alt="Docker" src="https://img.shields.io/badge/Docker-2496ED.svg?style=for-the-badge&logo=Docker&logoColor=white">
	<img alt="AWS" src="https://img.shields.io/badge/Amazon%20AWS-232F3E.svg?style=for-the-badge&logo=Amazon-AWS&logoColor=white">
	<img alt="Ubuntu" src="https://img.shields.io/badge/Ubuntu-E95420.svg?style=for-the-badge&logo=Ubuntu&logoColor=white">
</p>

<h4 align="center"> 
  🍔 Snackbar Management System 🍟
</h4>

<p align="center">
<a href="#about">About</a> •
<a href="#architecture">Architecture</a> •
<a href="#run">Running the Application</a> •
<a href="#endpoints">API Endpoints</a> •
<a href="#swagger">Swagger UI</a> •
<a href="#testing">Testing</a> •
<a href="#security">Security</a> •
<a href="#aws-integration">AWS Integration</a>
</p>
   
<p id="about">

## 💻 About the Project

The Snackbar Management System is a backend application designed to manage products and users in a snackbar environment. It follows Clean Architecture principles to ensure separation of concerns, maintainability, and testability.

### Key Features

- **Product Management**: Create, read, update, and delete snackbar products
- **User Management**: User registration, authentication, and authorization
- **JWT-based Authentication**: Secure API access with JSON Web Tokens
- **Event-Driven Architecture**: Domain events for important state changes
- **AWS Integration**: SQS integration for asynchronous message processing

### Technology Stack

- **Java 21**: Modern language features for robust development
- **Spring Boot**: Framework for building production-ready applications
- **MongoDB**: NoSQL database for flexible data storage
- **Docker**: Containerization for consistent deployment
- **Maven**: Dependency management and build automation
- **AWS SQS**: Message queue for asynchronous processing
- **JWT**: Token-based authentication and authorization
- **Ubuntu**: Container images based on Ubuntu 24.04 (Noble Numbat)

</p>

<p id="architecture">

## 🏛️ Architecture

This application implements Clean Architecture (also known as Hexagonal Architecture or Ports and Adapters), which separates the system into distinct layers with clear responsibilities:

### Architectural Layers

1. **Domain Layer**
   - Core business entities and logic
   - Business rules and domain events
   - No dependencies on external frameworks or libraries

2. **Application Layer**
   - Use cases that orchestrate domain entities
   - Input and output ports (interfaces)
   - Application services

3. **Infrastructure Layer**
   - Adapters for external systems (database, messaging, etc.)
   - Framework-specific implementations
   - Repository implementations
   - Controllers and API endpoints

### Key Components

- **Domain Entities**: `Product`, `User`
- **Use Cases**: Business operations like `CreateProductUseCase`, `AuthenticateUserUseCase`
- **Repositories**: Data access interfaces like `ProductRepository`, `UserRepository`
- **Controllers**: REST API endpoints in `ProductController`, `UserAuthController`
- **Event Publishers**: Domain event handling through `DomainEventPublisher`

### Package Structure

```
com.snackbar
├── product                  # Product domain
│   ├── domain               # Product domain entities and business rules
│   ├── application          # Product use cases and ports
│   └── infrastructure       # Product adapters and controllers
├── iam                      # Identity and Access Management domain
│   ├── domain               # User domain entities and business rules
│   ├── application          # Authentication use cases and ports
│   └── infrastructure       # Security configuration and controllers
└── infrastructure           # Shared infrastructure components
    └── messaging            # Messaging infrastructure (SQS)
```

### Database

The application can use one of two database configurations:

1. **Local MongoDB Container**:
   - Runs as part of the Docker Compose setup
   - Pre-configured with authentication
   - Automatically initialized with required collections

2. **MongoDB Atlas Cluster**:
   - Cloud-based MongoDB service
   - Provides high availability and automatic backups
   - Can be configured through environment variables

The MongoDB database name is "snackbar" and includes collections for products and users. The database requires authentication and comes pre-loaded with an administrative user and a regular user with read/write access to the "snackbar" database.

</p>

<p id="run">

## 🏃‍♂️ Running the Application

### Using Docker Compose

The easiest way to run the application locally is using Docker Compose:

```bash
# Clone the repository
git clone https://github.com/yourusername/snackbar-management.git
cd snackbar-management

# Build the application
mvn -f backend/pom.xml clean package -DskipTests

# Build and start the containers
docker compose up -d --build
```

This will start:
- MongoDB container
- Snackbar Management API container

The application will be available at http://localhost:8080

### Environment Variables

The application uses the following environment variables (defined in `.env` file):

- `MONGODB_USER`: MongoDB username
- `MONGODB_PASSWORD`: MongoDB password
- `DB_HOST`: MongoDB host
- `DB_PORT`: MongoDB port
- `APP_DB`: MongoDB database name
- `JWT_SECRET`: Secret key for JWT token generation
- `JWT_EXPIRES`: JWT token expiration time in milliseconds
- `AWS_REGION`: AWS region for SQS
- `AWS_SQS_PRODUCT_EVENTS_QUEUE_URL`: URL for the SQS product events queue

### Pipeline-based Provisioning

This project can also be provisioned using a CI/CD pipeline with: 
1. A Java Spring Boot application hosted on **EKS** (Amazon Elastic Kubernetes Service) from AWS
2. A MongoDB Atlas database
3. An Amazon API Gateway
4. A Lambda function working as an API Gateway Authorizer

All components can be provisioned using GitHub Actions pipeline.

#### Running the Pipeline Manually

To manually trigger the pipeline, follow these steps:

0. Ensure that these environment variables are correctly configured for the pipeline to work properly:

    AWS_ACCESS_KEY_ID
    AWS_DEFAULT_REGION
    AWS_SECRET_ACCESS_KEY
    AWS_SESSION_TOKEN
    MONGODBATLAS_ORG_PRIVATE_KEY
    MONGODBATLAS_ORG_PUBLIC_KEY
    ORG_ID

1. Navigate to the **snackbar-pipelines repository** where the pipeline is configured.
2. Click on the **"Actions"** tab located at the top of the repository.
3. In the list of workflows on the left, look for and select **"multi-stage-pipelines"**.
4. Once selected, click the **"Run workflow"** button on the right side of the page.
5. You can select the branch you want to run the pipeline on or use the main branch 
6. Click **"Run workflow"** to start the pipeline manually.

This will trigger the multi-stage pipeline and run the steps including build, test, and deploy stages based on the current code in the main branch.

To provision a homolog environment, commit to the homolog branch in the snackbar application repository. This way, the pipeline will provision a homolog docker image and homolog namespace in Kubernetes dedicated to this environment.

#### Kubernetes Specifications

**Technologies Used:**
- **Backend API:** Java Spring Boot (Java 21, using Maven 3.9.9).
- **Container Orchestration:** AWS EKS v1.31.
- **Package Management:** Helm v3.15.3.
- **IaC:** Terraform v1.10.3.

**System Components:**

Backend (Java Spring Boot):
- Configured as a **Deployment** and implemented via Helm Chart.
   - Main container **snackbar** used for the API service.
- Communication via port **8080** for APIs exposed by the Kubernetes Service.
- Secret configuration stored in **Kubernetes Secret**:
  - **snackbar Secret:** Stores the database access credentials, connection string, JWT token, and its validity period.
- **Horizontal Pod Autoscaling (HPA)** enabled for scaling based on CPU and memory usage.
- Lineness and Readiness probes configured for health checks.

The pipeline outputs the ALB URL to access application in the "appready" step of the pipeline.

### Running Outside of Container

If you need to run the application directly using Java and Maven:

1. Ensure you have Java 21 installed on your system:
   ```
   java -version
   ```

2. Make sure you have Maven 3.9.9 installed:
   ```
   mvn -version
   ```

3. Navigate to the project's root directory:
   ```bash
   # Build the project using Maven:
   mvn -f ./backend/pom.xml package

   # Run the application:
   java -jar ./backend/target/snackbar-0.0.1-SNAPSHOT.jar
   ```

</p>

<p id="endpoints">

## 📍 API Endpoints

> **Note:** Before testing the APIs, ensure you have [Postman](https://www.postman.com/) or a similar API testing tool installed on your system.

### Product Management

| Method | Endpoint                       | Description                      | Auth Required |
|--------|--------------------------------|----------------------------------|---------------|
| GET    | `/api/product`                 | List all products                | Yes           |
| GET    | `/api/product/id/{id}`         | Get product by ID                | Yes           |
| GET    | `/api/product/category/{cat}`  | Get products by category         | Yes           |
| GET    | `/api/product/name/{name}`     | Get product by name              | Yes           |
| POST   | `/api/product`                 | Create a new product             | Yes           |
| PUT    | `/api/product/id/{id}`         | Update a product                 | Yes           |
| DELETE | `/api/product/id/{id}`         | Delete a product                 | Yes           |

### User Management

| Method | Endpoint                       | Description                      | Auth Required |
|--------|--------------------------------|----------------------------------|---------------|
| POST   | `/api/user/auth/signup`        | Register a new user              | No            |
| POST   | `/api/user/auth/login`         | Authenticate user                | No            |
| GET    | `/api/user/`                   | List all users                   | Yes           |
| GET    | `/api/user/cpf/{cpf}`          | Get user by CPF                  | Yes           |
| PUT    | `/api/user/{id}`               | Update user                      | Yes           |
| DELETE | `/api/user/{id}`               | Delete user                      | Yes           |

### Health Check

| Method | Endpoint                       | Description                      | Auth Required |
|--------|--------------------------------|----------------------------------|---------------|
| GET    | `/actuator/health`             | Check application health         | No            |

</p>

<p id="swagger">

## 📄 Swagger UI

You can access the Swagger UI to explore and test the APIs interactively at:

```
http://[host]/swagger-ui.html
```

Where `[host]` depends on your deployment:
- Local development: `localhost:8080`
- EKS with ALB: The ALB endpoint provided by the pipeline

Swagger UI provides:
- Interactive documentation for all API endpoints
- Request and response schema details
- The ability to test endpoints directly from the browser
- Authentication support for protected endpoints

</p>

<p id="testing">

## 🧪 Testing

### Unit Tests

The application includes comprehensive unit tests for all layers:

```bash
# Run unit tests
mvn test
```

### Integration Tests

Integration tests are available to test the application with its dependencies:

```bash
# Run integration tests
./product_test_integration.sh  # For product management
./iam_test_integration_v3.sh   # For user management
```

### Postman Collection

A Postman collection is provided for manual API testing:

1. Import the `snackbar-management-postman-collection-updated.json` file into Postman
2. Set up an environment with the following variables:
   - `base_url`: `http://localhost:8080`
   - `user_cpf`: `52998224725`
   - `user_password`: `Password123!`
   - `user_new_password`: `NewPassword123!`
   - `product_category`: `Bebida`
   - `product_name`: `Hambúrguer`

The collection follows a natural user journey:
1. System health check
2. User registration and authentication
3. Product browsing
4. Product management
5. User profile management
6. Admin operations
7. Cleanup

</p>

<p id="security">

## 🔒 Security

### Authentication

The application uses JWT (JSON Web Token) for authentication:

1. Users register with email, CPF, password, and role
2. Upon login, a JWT token is issued
3. The token must be included in the `Authorization` header for protected endpoints

### Authorization

Different user roles have different permissions:

- `ADMIN`: Full access to all endpoints
- `CONSUMER`: Limited access to product viewing and user profile management
- Anonymous users: Access only to registration and login endpoints

### Password Security

- Passwords are stored using BCrypt hashing
- Password complexity requirements are enforced

</p>

<p id="aws-integration">

## ☁️ AWS Integration

### SQS Integration

The application integrates with AWS SQS for asynchronous processing:

- Product creation, update, and deletion events are published to SQS
- SQS messages are consumed by the application for event processing

### Configuration

AWS integration requires proper configuration:

```properties
aws.region=${AWS_REGION}
aws.sqs.product.events.queue.url=${AWS_SQS_PRODUCT_EVENTS_QUEUE_URL}
aws.endpoint.url=${AWS_ENDPOINT_URL}
aws.sqs.polling-enabled=${AWS_SQS_POLLING_ENABLED}
aws.sqs.polling-delay-ms=${AWS_SQS_POLLING_DELAY_MS}
aws.sqs.max-messages=${AWS_SQS_MAX_MESSAGES}
aws.sqs.wait-time-seconds=${AWS_SQS_WAIT_TIME_SECONDS}
```

</p>

## 📝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🙏 Acknowledgments

- FIAP for the project requirements and guidance
- All contributors who have helped shape this project
