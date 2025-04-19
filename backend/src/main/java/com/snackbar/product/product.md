# Product Module

The Product module is responsible for managing the products offered by the snack bar. It follows a clean architecture approach with distinct layers for domain, application, and infrastructure.

## Domain Layer

### Entities

#### Product

The `Product` entity is a record class that represents a product in the snack bar. It has the following attributes:

- `id`: Unique identifier for the product
- `name`: Name of the product (minimum 3 characters)
- `category`: Category of the product (must be one of: "Lanche", "Acompanhamento", "Bebida", "Sobremesa")
- `description`: Description of the product (minimum 10 characters)
- `price`: Price of the product (must be greater than zero)
- `cookingTime`: Time in minutes required to prepare the product (must be zero or positive)

The entity includes validation logic to ensure all data meets business requirements.

### Exceptions

- `InvalidProductDataException`: Thrown when product data doesn't meet validation requirements
- `ProductNotFoundException`: Thrown when a product cannot be found in the repository

## Application Layer

### Use Cases

The application layer contains the following use cases:

1. `CreateProductUseCase`: Creates a new product
2. `GetProductByIdUseCase`: Retrieves a product by its ID
3. `GetProductByNameUseCase`: Retrieves a product by its name
4. `GetProductByCategoryUseCase`: Retrieves products by category
5. `ListProductUseCase`: Lists all available products
6. `UpdateProductByIdUseCase`: Updates an existing product
7. `DeleteProductByIdUseCase`: Deletes a product by its ID

## Infrastructure Layer

### Controllers

The `ProductController` exposes the following REST endpoints:

- `POST /api/product`: Create a new product
- `GET /api/product`: List all products
- `GET /api/product/id/{id}`: Get a product by ID
- `GET /api/product/name/{name}`: Get a product by name
- `GET /api/product/category/{category}`: Get products by category
- `PUT /api/product/id/{id}`: Update a product
- `DELETE /api/product/id/{id}`: Delete a product

### Persistence

- `ProductEntity`: MongoDB document representation of a product
- `ProductRepository`: Spring Data MongoDB repository for product persistence
- `ProductRepositoryGateway`: Implementation of the repository pattern for products

## Testing

The Product module has comprehensive test coverage:

- **Domain Tests**: 100% coverage of the `Product` entity, including all validation rules
- **Application Tests**: Tests for all use cases with mocked repositories
- **Integration Tests**: Tests for the repository gateway and controllers

### Test Coverage

The `Product` entity has 100% test coverage, including:
- Successful product creation
- Name validation (null, empty, too short)
- Category validation (null, empty, invalid)
- Description validation (null, empty, too short)
- Price validation (null, zero, negative)
- Cooking time validation (null, negative, zero)

## Running Tests

To run the tests for the Product module:

```bash
mvn -f backend/pom.xml test
```

To generate a test coverage report:

```bash
mvn -f backend/pom.xml clean test jacoco:report
```

The coverage report will be available at `backend/target/site/jacoco/index.html`.

## 📍Products Endpoints

✅ All endpoints below have been implemented with standardized responses in `/api/product`:

| route               | description                                          | status
|----------------------|-----------------------------------------------------|--------
| <kbd>GET /api/product</kbd>     | See [request details](#get-products) | ✅ Done
| <kbd>GET /api/product/id/{id}</kbd>     |  See [request details](#get-products-id) | ✅ Done
| <kbd>GET /api/product/category/{category}</kbd>     |See [request details](#get-products-category) | ✅ Done
| <kbd>POST /api/product</kbd>     | See [request details](#post-products) | ✅ Done
| <kbd>PUT /api/product/id/{id}</kbd>     | See [request details](#put-products) | ✅ Done
| <kbd>DELETE /api/product/id/{id}</kbd>     | See [request details](#delete-products) | ✅ Done


<h3 id="get-products">GET /api/product ✅</h3>

**RESPONSE**  
```json
{
    "success": true,
    "message": "Products retrieved successfully",
    "data": [
        {
            "id": "671bb29c52801c1c1efe6911",
            "category": "Lanche",
            "description": "Hambúrguer artesanal 160g, servido com pão de brioche, alface e tomate.",
            "name": "Hambúrguer",
            "price": 22,
            "cookingTime": 10
        }
        /* All other products */
    ]
}
```

<h3 id="get-products-id">GET /api/product/id/{id} ✅</h3>

**RESPONSE**
```json
{
    "success": true,
    "message": "Product retrieved successfully",
    "data": {
        "id": "671d1ab834d76230acfe6911",
        "category": "Lanche",
        "description": "Hambúrguer artesanal 160g, servido com pão de brioche, alface e tomate.",
        "name": "Hambúrguer",
        "price": 22,
        "cookingTime": 10
    }
}
```

<h3 id="get-products-category">GET /api/product/category/{category} ✅</h3>

**RESPONSE**
```json
{
    "success": true,
    "message": "Products retrieved successfully",
    "data": [
        {
            "id": "671d1ab834d76230acfe6911",
            "category": "Lanche",
            "description": "Hambúrguer artesanal 160g, servido com pão de brioche, alface e tomate.",
            "name": "Hambúrguer",
            "price": 22,
            "cookingTime": 10
        },
        {
            "id": "67266201b5ad4f0589fe6912",
            "category": "Lanche",
            "description": "Hambúrguer artesanal 160g, servido com pão de brioche e queijo prato.",
            "name": "Cheesebúrguer",
            "price": 25,
            "cookingTime": 10
        }
        /* All other products in the same category */
    ]
}
```
<h3 id="post-products">POST /api/product ✅</h3>

**REQUEST**  
```json
{
    "name": "Hambúrguer",
    "category": "Lanche",
    "description": "Hambúrguer artesanal 160g, servido com pão de brioche, alface e tomate.",
    "price": 22,
    "cookingTime": 10
}
```
**RESPONSE**
```json
{
    "success": true,
    "message": "Product created successfully",
    "data": {
        "id": "671d1c91f7689b2849534586",
        "category": "Lanche",
        "description": "Hambúrguer artesanal 160g, servido com pão de brioche, alface e tomate.",
        "name": "Hambúrguer",
        "price": 22,
        "cookingTime": 10
    }
}
```

<h3 id="put-products">PUT /api/product/id/{id} ✅</h3>

**REQUEST**  
```json
{
    "id": "67266201b5ad4f0589fe6917",
    "category": "Acompanhamento",
    "description": "Porção grande de batatas fritas crocantes.",
    "name": "Batata frita Grande",
    "price": 15,
    "cookingTime": 12
}
```

**RESPONSE**  
```json
{
    "success": true,
    "message": "Product updated successfully",
    "data": {
        "id": "67266201b5ad4f0589fe6917",
        "category": "Acompanhamento",
        "description": "Porção grande de batatas fritas crocantes.",
        "name": "Batata frita Grande",
        "price": 15,
        "cookingTime": 12
    }
}
```
<h3 id="delete-products">DELETE /api/product/id/{id} ✅</h3>

**RESPONSE**  
```json
{
    "success": true,
    "message": "Product deleted successfully",
    "data": null
}
```
