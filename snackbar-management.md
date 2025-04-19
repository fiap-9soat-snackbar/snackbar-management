# Snackbar Management Microservice

## Overview

The Snackbar Management microservice is a Spring Boot application designed to manage a snack bar's operations. It follows clean architecture principles and is built with a domain-driven design approach. The service is containerized using Docker and uses MongoDB as its database.

## Architecture

The application follows a clean architecture with the following layers:

1. **Domain Layer**: Contains business entities, rules, and exceptions
2. **Application Layer**: Contains use cases that orchestrate the business logic
3. **Infrastructure Layer**: Contains implementations of repositories, controllers, and external services

### Key Components

- **Product Module**: Manages the products offered by the snack bar
- **MongoDB**: NoSQL database for storing product information
- **Spring Boot**: Framework for building the REST API
- **Docker**: Containerization for easy deployment

## Project Structure

```
backend/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── snackbar/
│   │   │           ├── product/
│   │   │           │   ├── application/
│   │   │           │   │   └── usecases/
│   │   │           │   ├── domain/
│   │   │           │   │   ├── entity/
│   │   │           │   │   └── exceptions/
│   │   │           │   └── infrastructure/
│   │   │           │       ├── config/
│   │   │           │       ├── controllers/
│   │   │           │       ├── gateways/
│   │   │           │       └── persistence/
│   │   └── resources/
│   └── test/
│       └── java/
│           └── com/
│               └── snackbar/
│                   └── product/
│                       ├── application/
│                       │   └── usecases/
│                       └── domain/
│                           └── entity/
└── pom.xml
```

## Features

- **Product Management**: CRUD operations for products
- **Category Filtering**: Ability to filter products by category
- **Data Validation**: Comprehensive validation of product data
- **Standardized Responses**: Consistent API response format

## API Documentation

The API provides the following endpoints:

- `GET /api/product`: List all products
- `GET /api/product/id/{id}`: Get a product by ID
- `GET /api/product/name/{name}`: Get a product by name
- `GET /api/product/category/{category}`: Get products by category
- `POST /api/product`: Create a new product
- `PUT /api/product/id/{id}`: Update a product
- `DELETE /api/product/id/{id}`: Delete a product

## Running the Application

### Prerequisites

- Docker and Docker Compose
- Java 21
- Maven

### Steps

1. Clone the repository
2. Build the application:
   ```bash
   mvn -f backend/pom.xml clean package
   ```
3. Start the containers:
   ```bash
   docker compose up --build -d
   ```
4. The API will be available at `http://localhost:8080/api/product`

## Testing

The application includes comprehensive tests:

- Unit tests for domain entities and use cases
- Integration tests for repositories and controllers

To run the tests:

```bash
mvn -f backend/pom.xml test
```

To generate a test coverage report:

```bash
mvn -f backend/pom.xml clean test jacoco:report
```

## Development Tools

- **Testing Scripts**: The repository includes two scripts for testing:
  - `full_test.sh`: Cleans up the environment, rebuilds the application, and tests all endpoints
  - `api_test.sh`: Tests all API endpoints assuming the application is already running

## Best Practices

- **Clean Architecture**: Separation of concerns with distinct layers
- **Domain-Driven Design**: Focus on the core domain and business logic
- **Test-Driven Development**: Comprehensive test coverage
- **Docker Containerization**: Consistent deployment environment
- **Standardized API Responses**: Consistent response format for all endpoints
