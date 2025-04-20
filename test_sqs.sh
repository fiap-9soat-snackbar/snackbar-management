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

echo "=== Starting containers with SQS enabled ==="
# Rebuild and start the containers (will use SPRING_PROFILES_ACTIVE from .env file)
docker compose up --build -d

echo "=== Waiting for application to start ==="
# Wait for the application to be ready
sleep 20

echo "=== Testing SQS Event Publishing ==="
# Create a product to trigger an event
echo "1. Creating product to trigger event"
PRODUCT_ID=$(curl -s -X POST -H "Content-Type: application/json" \
  -d '{"name":"SQS Test Product","category":"Lanche","description":"Product to test SQS event publishing","price":9.99,"cookingTime":1}' \
  http://localhost:8080/api/product | jq -r '.data.id')
echo "Created product with ID: $PRODUCT_ID"

# Update the product to trigger another event
echo "2. Updating product to trigger another event"
curl -s -X PUT -H "Content-Type: application/json" \
  -d '{"name":"SQS Test Product Updated","category":"Lanche","description":"Updated product to test SQS event publishing","price":19.99,"cookingTime":2}' \
  "http://localhost:8080/api/product/id/$PRODUCT_ID" | jq

# Delete the product to trigger a third event
echo "3. Deleting product to trigger a third event"
curl -s -X DELETE "http://localhost:8080/api/product/id/$PRODUCT_ID" | jq

echo "=== Check application logs for SQS publishing information ==="
docker compose logs app | grep "Event published to SQS"

echo "=== All tests completed ==="
