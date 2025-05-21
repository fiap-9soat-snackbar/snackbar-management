# OpenAPI Documentation Plan for Snackbar Management Application

This document outlines a comprehensive approach to implementing OpenAPI documentation in our Snackbar Management application while respecting clean architecture principles.

## Table of Contents

1. [Documentation Strategy](#documentation-strategy)
2. [Centralized Documentation Configuration](#centralized-documentation-configuration)
3. [Controller Layer Documentation](#controller-layer-documentation)
4. [DTO Documentation](#dto-documentation)
5. [Domain Layer Documentation](#domain-layer-documentation)
6. [Implementation Plan](#implementation-plan)
7. [Best Practices](#best-practices)

## Documentation Strategy

Our documentation strategy follows a hybrid approach:

- **Centralized configuration** for global API information
- **In-code annotations** for specific endpoints and DTOs, but only in the outer layers
- **Clean architecture compliance** by avoiding framework dependencies in inner layers

## Centralized Documentation Configuration

Create a dedicated configuration class in the infrastructure layer:

```java
@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Snackbar Management API")
                .version("1.0.0")
                .description("API for managing a snackbar's products, orders, and user authentication")
                .contact(new Contact()
                    .name("FIAP 9SOAT Team")
                    .url("https://github.com/fiap-9soat-snackbar"))
                .license(new License().name("MIT License")))
            .externalDocs(new ExternalDocumentation()
                .description("Snackbar Management Documentation")
                .url("https://github.com/fiap-9soat-snackbar/documentation"))
            .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
            .components(new Components()
                .addSecuritySchemes("bearerAuth", 
                    new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .description("JWT token authentication")))
            .addTagsItem(new Tag().name("Product Management").description("Operations related to products"))
            .addTagsItem(new Tag().name("User Management").description("Operations related to user management"))
            .addTagsItem(new Tag().name("Authentication").description("Authentication operations"))
            .addTagsItem(new Tag().name("Health").description("Health check endpoints"));
    }
    
    // Define common responses that can be reused across the API
    @Bean
    public OperationCustomizer customGlobalResponses() {
        return (operation, handlerMethod) -> {
            operation.addApiResponse("401", new ApiResponse().description("Unauthorized - Authentication required"));
            operation.addApiResponse("403", new ApiResponse().description("Forbidden - Insufficient permissions"));
            operation.addApiResponse("500", new ApiResponse().description("Internal server error"));
            return operation;
        };
    }
}
```

## Controller Layer Documentation

Apply OpenAPI annotations directly to controllers in the infrastructure layer:

### Product Controller Example

```java
@RestController
@RequestMapping("/api/product")
@Tag(name = "Product Management")
public class ProductController {
    
    private final CreateProductUseCase createProductUseCase;
    // Other use cases...
    
    @Operation(
        summary = "Create a new product", 
        description = "Creates a new product with the provided details",
        responses = {
            @ApiResponse(
                responseCode = "200", 
                description = "Product created successfully",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResponseDTO.class),
                    examples = @ExampleObject(
                        value = """
                        {
                          "success": true,
                          "message": "Product created successfully",
                          "data": {
                            "id": "60f1a5b3e8c7a12345678901",
                            "name": "Cheeseburger",
                            "category": "SANDWICH",
                            "price": 25.90,
                            "cookingTime": 15
                          }
                        }
                        """
                    )
                )
            ),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
        }
    )
    @PostMapping
    public ResponseEntity<ResponseDTO> createProduct(@RequestBody CreateProductRequest request) {
        // Implementation
    }
    
    // Other methods with similar annotations
}
```

## DTO Documentation

Apply OpenAPI annotations to DTOs in the infrastructure layer:

```java
@Schema(description = "Request object for creating a new product")
public class CreateProductRequest {
    
    @Schema(description = "Name of the product", example = "Cheeseburger", required = true)
    private String name;
    
    @Schema(description = "Category of the product", example = "SANDWICH", required = true)
    private String category;
    
    @Schema(description = "Detailed description of the product", example = "Delicious burger with cheese")
    private String description;
    
    @Schema(description = "Price of the product in BRL", example = "25.90", required = true)
    private BigDecimal price;
    
    @Schema(description = "Estimated cooking time in minutes", example = "15")
    private Integer cookingTime;
    
    // Getters and setters
}
```

## Domain Layer Documentation

For domain entities and use cases, we need to avoid direct OpenAPI annotations to maintain clean architecture principles.

### Alternative 1: Documentation Through Mappers

Document domain models indirectly through DTOs and mappers:

```java
// In the infrastructure layer
@Schema(description = "Product information")
public class ProductResponseDTO {
    // Fields with OpenAPI annotations
    
    // Mapper method that documents the relationship to domain entity
    public static ProductResponseDTO fromDomain(Product product) {
        // Mapping logic
    }
}
```

### Alternative 2: JavaDoc Comments

Use standard JavaDoc for domain entities and use cases:

```java
/**
 * Represents a product in the snackbar menu.
 * <p>
 * This entity contains all the core information about a product,
 * including its name, category, price, and preparation time.
 * </p>
 */
public class Product {
    private String id;
    
    /**
     * The name of the product as displayed on the menu.
     */
    private String name;
    
    /**
     * The category this product belongs to (e.g., SANDWICH, SIDE, BEVERAGE).
     */
    private String category;
    
    // Other fields and methods
}
```

## Implementation Plan

1. **Phase 1: Setup Basic Documentation Infrastructure**
   - Create OpenApiConfig class
   - Define global API information
   - Configure security schemes
   - Set up common response patterns

2. **Phase 2: Document Controllers and DTOs**
   - Add annotations to all REST controllers
   - Document request/response DTOs
   - Add examples for common operations
   - Ensure proper tagging of endpoints

3. **Phase 3: Enhance Documentation with Examples**
   - Add realistic examples for all endpoints
   - Document error scenarios
   - Include authentication examples

4. **Phase 4: Review and Testing**
   - Verify documentation accuracy
   - Test Swagger UI functionality
   - Ensure all endpoints are properly documented
   - Check that clean architecture principles are maintained

## Best Practices

1. **Maintain Clean Architecture**
   - Keep OpenAPI annotations in the infrastructure layer only
   - Use JavaDoc for domain layer documentation
   - Avoid framework dependencies in inner layers

2. **Documentation Quality**
   - Use clear, concise descriptions
   - Provide realistic examples
   - Document error scenarios
   - Keep documentation up-to-date with code changes

3. **Consistent Naming and Structure**
   - Use consistent naming conventions
   - Group related endpoints with tags
   - Maintain consistent response structures

4. **Security Documentation**
   - Clearly document authentication requirements
   - Specify required permissions for each endpoint
   - Document token usage and expiration

5. **Versioning**
   - Include API version information
   - Document breaking changes between versions

By following this plan, we can create comprehensive API documentation while maintaining the integrity of our clean architecture design.
