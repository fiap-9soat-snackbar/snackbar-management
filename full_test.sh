#!/bin/bash
set -e

echo "=== Cleaning up environment ==="
# Remove all containers, volumes, and images
docker compose down -v --rmi all
# Delete build artifacts
rm -rf backend/target

echo "=== Building application ==="
# Compile the application
mvn -f backend/pom.xml clean package

echo "=== Starting containers ==="
# Rebuild and start the containers
docker compose up --build -d

echo "=== Waiting for application to start ==="
# Wait for the application to be ready
sleep 15

echo "=== Testing API endpoints ==="
# Get all products
echo "1. Testing GET /api/product"
curl -s http://localhost:8080/api/product | jq

# Get products by category
echo "2. Testing GET /api/product/category/{category}"
curl -s http://localhost:8080/api/product/category/Bebida | jq

# Get product by name
echo "3. Testing GET /api/product/name/{name}"
curl -s "http://localhost:8080/api/product/name/Brownie" | jq

# Create a new product
echo "4. Testing POST /api/product"
PRODUCT_ID=$(curl -s -X POST -H "Content-Type: application/json" \
  -d '{"name":"Teste Produto","category":"Sobremesa","description":"Produto de teste para verificar a API","price":15.99,"cookingTime":3}' \
  http://localhost:8080/api/product | jq -r '.data.id')
echo "Created product with ID: $PRODUCT_ID"

# Get the created product
echo "5. Testing GET /api/product/name/{name} for created product"
curl -s "http://localhost:8080/api/product/name/Teste%20Produto" | jq

# Update the product
echo "6. Testing PUT /api/product/id/{id}"
curl -s -X PUT -H "Content-Type: application/json" \
  -d '{"name":"Teste Produto Atualizado","category":"Sobremesa","description":"Produto de teste para verificar a API - atualizado","price":18.99,"cookingTime":5}' \
  "http://localhost:8080/api/product/id/$PRODUCT_ID" | jq

# Get the updated product
echo "7. Testing GET /api/product/name/{name} for updated product"
curl -s "http://localhost:8080/api/product/name/Teste%20Produto%20Atualizado" | jq

# Delete the product
echo "8. Testing DELETE /api/product/id/{id}"
curl -s -X DELETE "http://localhost:8080/api/product/id/$PRODUCT_ID" | jq

# Verify deletion
echo "9. Verifying deletion"
curl -s "http://localhost:8080/api/product/name/Teste%20Produto%20Atualizado"

echo "=== All tests completed ==="
