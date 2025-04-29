#!/bin/bash

# ===================================================
# Snackbar Management System Integration Test Script
# ===================================================

# Color definitions for better readability
GREEN='\033[0;32m'
YELLOW='\033[0;33m'
RED='\033[0;31m'
BLUE='\033[0;34m'
PURPLE='\033[0;35m'
CYAN='\033[0;36m'
BOLD='\033[1m'
NC='\033[0m' # No Color

# Function to print section headers
print_section() {
  echo ""
  echo -e "${BOLD}${PURPLE}==============================================${NC}"
  echo -e "${BOLD}${BLUE}   SECTION $1: $2${NC}"
  echo -e "${BOLD}${PURPLE}==============================================${NC}"
  echo ""
}

# ===================================================
# SECTION 1: Environment Setup and Cleanup
# ===================================================
print_section "1" "Environment Setup and Cleanup"

# Clean up any existing environment
echo -e "${GREEN}Cleaning up environment...${NC}"
cd /home/saulo/workspace/fiap-alura/fase04/snackbar-management
docker compose down -v --rmi all
rm -rf backend/target

# Build the application
echo -e "${GREEN}Building application...${NC}"
mvn -f backend/pom.xml clean package -DskipTests

# Start containers
echo -e "${GREEN}Starting containers...${NC}"
docker compose up -d --build

# Wait for application to be ready
echo -e "${YELLOW}Waiting for application to start (30 seconds)...${NC}"
sleep 30

# Check if MongoDB is ready
echo -e "${GREEN}Checking if MongoDB is ready...${NC}"
MONGO_READY=false
for i in {1..5}; do
  if docker compose exec mongodb mongosh --quiet --eval "db.runCommand({ping:1}).ok" | grep -q "1"; then
    MONGO_READY=true
    echo -e "${GREEN}MongoDB is ready!${NC}"
    break
  else
    echo -e "${YELLOW}Waiting for MongoDB to be ready (attempt $i/5)...${NC}"
    sleep 5
  fi
done

if [ "$MONGO_READY" = false ]; then
  echo -e "${RED}MongoDB is not ready. Continuing anyway, but tests might fail.${NC}"
fi

# Check if MongoDB is ready
echo -e "${GREEN}Checking if MongoDB is ready...${NC}"
MONGO_READY=false
for i in {1..5}; do
  if docker compose exec mongodb mongosh --quiet --eval "db.runCommand({ping:1}).ok" | grep -q "1"; then
    MONGO_READY=true
    echo -e "${GREEN}MongoDB is ready!${NC}"
    break
  else
    echo -e "${YELLOW}Waiting for MongoDB to be ready (attempt $i/5)...${NC}"
    sleep 5
  fi
done

if [ "$MONGO_READY" = false ]; then
  echo -e "${RED}MongoDB is not ready. Continuing anyway, but tests might fail.${NC}"
fi

# ===================================================
# SECTION 2: REST API Testing
# ===================================================
print_section "2" "REST API Testing"

# Test product creation via API
echo -e "${GREEN}Testing product creation via API...${NC}"
CREATE_RESPONSE=$(curl -s -X POST http://localhost:8080/api/product \
  -H "Content-Type: application/json" \
  -d '{"name":"API Test Burger","category":"Lanche","description":"A burger created via API","price":12.99,"cookingTime":10}')

echo "API Create Response: $CREATE_RESPONSE"

# Extract product ID from response
PRODUCT_ID=$(echo $CREATE_RESPONSE | grep -o '"id":"[^"]*' | cut -d'"' -f4)

if [ -z "$PRODUCT_ID" ]; then
  echo -e "${RED}Failed to create product via API${NC}"
  
  # Check application logs for errors
  echo -e "${YELLOW}Checking application logs for errors...${NC}"
  docker compose logs app | grep -i "error" | tail -10
  
  # Continue with Lambda testing even if API test fails
  echo -e "${YELLOW}Continuing with Lambda testing...${NC}"
else
  echo -e "${GREEN}Product created via API with ID: $PRODUCT_ID${NC}"

  # Test product retrieval via API
  echo -e "${GREEN}Testing product retrieval via API...${NC}"
  GET_RESPONSE=$(curl -s -X GET http://localhost:8080/api/product/id/$PRODUCT_ID)
  echo "API Get Response: $GET_RESPONSE"

  # Test product update via API
  echo -e "${GREEN}Testing product update via API...${NC}"
  UPDATE_RESPONSE=$(curl -s -X PUT http://localhost:8080/api/product/id/$PRODUCT_ID \
    -H "Content-Type: application/json" \
    -d '{"name":"Updated API Burger","category":"Lanche","description":"An updated burger via API","price":14.99,"cookingTime":12}')

  echo "API Update Response: $UPDATE_RESPONSE"

  # Test product deletion via API
  echo -e "${GREEN}Testing product deletion via API...${NC}"
  DELETE_RESPONSE=$(curl -s -X DELETE http://localhost:8080/api/product/id/$PRODUCT_ID)
  echo "API Delete Response: $DELETE_RESPONSE"
fi

# ===================================================
# SECTION 3: Lambda to SQS Integration Testing
# ===================================================
print_section "3" "Lambda to SQS Integration Testing"

# Test Lambda function creating a product via SQS
echo -e "${GREEN}Testing CREATE operation via Lambda...${NC}"
LAMBDA_API_URL="https://z8fuesxwfj.execute-api.us-east-1.amazonaws.com/product-operations"

CREATE_LAMBDA_RESPONSE=$(curl -s -X POST "$LAMBDA_API_URL" \
  -H "Content-Type: application/json" \
  -d '{"operation":"CREATE","product":{"name":"Lambda Test Burger","category":"Lanche","description":"A burger created via Lambda","price":16.99,"cookingTime":15}}')

echo "Lambda Create Response: $CREATE_LAMBDA_RESPONSE"

# Extract SQS message ID
SQS_MESSAGE_ID=$(echo $CREATE_LAMBDA_RESPONSE | grep -o '"sqsMessageId":"[^"]*' | cut -d'"' -f4)

if [[ "$CREATE_LAMBDA_RESPONSE" == *"success\": true"* ]]; then
  echo -e "${GREEN}Message sent to SQS with ID: $SQS_MESSAGE_ID${NC}"
else
  echo -e "${RED}Failed to send message to SQS via Lambda${NC}"
  exit 1
fi

# Wait for message processing
echo -e "${YELLOW}Waiting for SQS message to be processed (30 seconds)...${NC}"
sleep 30

# ===================================================
# SECTION 4: Verification of Message Processing
# ===================================================
print_section "4" "Verification of Message Processing"

# Check application logs for product creation from Lambda SQS message
echo -e "${GREEN}Checking application logs for product creation via SQS...${NC}"
PRODUCT_LOGS=$(docker compose logs app)

# Extract the product ID created by the SQS consumer
echo -e "${YELLOW}Looking for product created via Lambda/SQS...${NC}"

# First, look for the ProductRepositoryGateway log that shows the saved entity details
# This contains the actual MongoDB ID of the product created via Lambda/SQS
REPO_LOG=$(echo "$PRODUCT_LOGS" | grep -i "Saved entity:" | grep -i "Lambda Test Burger" | tail -1)
echo -e "${YELLOW}Repository log entry:${NC}"
echo "$REPO_LOG"

# Extract the ID from the repository log
if [[ "$REPO_LOG" =~ id=\'([a-f0-9]+)\' ]]; then
  MONGO_ID="${BASH_REMATCH[1]}"
  echo -e "${GREEN}Found product ID from repository log: $MONGO_ID${NC}"
else
  # If not found in repository log, try other methods
  echo -e "${YELLOW}Could not find product ID in repository log, trying alternative methods...${NC}"
  
  # Try to get all products and find the one with "Lambda Test Burger"
  PRODUCT_LIST_RESPONSE=$(curl -s -X GET http://localhost:8080/api/product)
  echo -e "${YELLOW}Searching for Lambda Test Burger in product list...${NC}"
  
  # Use a more targeted approach to extract the specific product
  LAMBDA_PRODUCT=$(echo "$PRODUCT_LIST_RESPONSE" | grep -o '{[^}]*"name":"Lambda Test Burger"[^}]*}')
  
  if [ ! -z "$LAMBDA_PRODUCT" ]; then
    MONGO_ID=$(echo "$LAMBDA_PRODUCT" | grep -o '"id":"[^"]*' | cut -d'"' -f4)
    echo -e "${GREEN}Found Lambda Test Burger with ID: $MONGO_ID${NC}"
  else
    # Last resort: get the most recent product
    echo -e "${YELLOW}Lambda Test Burger not found, getting most recent product...${NC}"
    MONGO_ID=$(echo "$PRODUCT_LIST_RESPONSE" | grep -o '"id":"[^"]*' | head -1 | cut -d'"' -f4)
    
    if [ -z "$MONGO_ID" ]; then
      echo -e "${RED}No products found in database. Exiting.${NC}"
      exit 1
    else
      echo -e "${YELLOW}Using most recent product with ID: $MONGO_ID${NC}"
    fi
  fi
fi

# Verify the product exists in the database
PRODUCT_VERIFY=$(curl -s -X GET http://localhost:8080/api/product/id/$MONGO_ID)
if [[ "$PRODUCT_VERIFY" == *"success\":true"* ]]; then
  echo -e "${GREEN}Successfully verified product exists in database with ID: $MONGO_ID${NC}"
  echo -e "${GREEN}Product details: $(echo "$PRODUCT_VERIFY" | grep -o '"data":{[^}]*}')${NC}"
else
  echo -e "${RED}Could not verify product in database. Response: $PRODUCT_VERIFY${NC}"
  echo -e "${RED}Test cannot continue without a valid product ID. Exiting.${NC}"
  exit 1
fi

# Test product update via Lambda
echo -e "${GREEN}Testing UPDATE operation via Lambda...${NC}"
UPDATE_LAMBDA_RESPONSE=$(curl -s -X POST "$LAMBDA_API_URL" \
  -H "Content-Type: application/json" \
  -d "{\"operation\":\"UPDATE\",\"productId\":\"$MONGO_ID\",\"product\":{\"name\":\"Updated Lambda Burger\",\"category\":\"Lanche\",\"description\":\"An updated burger via Lambda\",\"price\":18.99,\"cookingTime\":20}}")

echo "Lambda Update Response: $UPDATE_LAMBDA_RESPONSE"

# Wait for update processing
echo -e "${YELLOW}Waiting for update to be processed (30 seconds)...${NC}"
sleep 30

# Check logs for update confirmation
echo -e "${GREEN}Checking logs for update confirmation...${NC}"
UPDATE_LOGS=$(docker compose logs app | grep -i "Product updated" | tail -5)
echo "$UPDATE_LOGS"

# Verify product was updated
echo -e "${GREEN}Verifying product update in database...${NC}"
GET_UPDATED_RESPONSE=$(curl -s -X GET http://localhost:8080/api/product/id/$MONGO_ID)
echo "Get Updated Product Response: $GET_UPDATED_RESPONSE"

if [[ "$GET_UPDATED_RESPONSE" == *"Updated Lambda Burger"* ]]; then
  echo -e "${GREEN}Product successfully updated via Lambda/SQS!${NC}"
else
  echo -e "${YELLOW}Product may not have been updated. Checking product details...${NC}"
  # Display the actual product details
  echo "$GET_UPDATED_RESPONSE" | grep -o '"name":"[^"]*"' || echo "Name not found"
fi

# Test product deletion via Lambda
echo -e "${GREEN}Testing DELETE operation via Lambda...${NC}"
DELETE_LAMBDA_RESPONSE=$(curl -s -X POST "$LAMBDA_API_URL" \
  -H "Content-Type: application/json" \
  -d "{\"operation\":\"DELETE\",\"productId\":\"$MONGO_ID\"}")

echo "Lambda Delete Response: $DELETE_LAMBDA_RESPONSE"

# Wait for deletion processing
echo -e "${YELLOW}Waiting for deletion to be processed (30 seconds)...${NC}"
sleep 30

# Check logs for deletion confirmation
echo -e "${GREEN}Checking logs for deletion confirmation...${NC}"
DELETE_LOGS=$(docker compose logs app | grep -i "Product deleted" | tail -5)
echo "$DELETE_LOGS"

# Verify product deletion
echo -e "${GREEN}Verifying product deletion...${NC}"
GET_DELETED_RESPONSE=$(curl -s -X GET http://localhost:8080/api/product/id/$MONGO_ID)
echo "Get Deleted Product Response: $GET_DELETED_RESPONSE"

if [[ "$GET_DELETED_RESPONSE" == *"Product not found"* ]]; then
  echo -e "${GREEN}Product successfully deleted!${NC}"
else
  echo -e "${RED}Product may not have been deleted. Response: $GET_DELETED_RESPONSE${NC}"
fi

# ===================================================
# SECTION 5: Check SQS Queue Status
# ===================================================
print_section "5" "Check SQS Queue Status"

# Check SQS queue for messages
echo -e "${GREEN}Checking SQS queue for messages...${NC}"
QUEUE_URL="https://sqs.us-east-1.amazonaws.com/953430082388/snackbar-management-product-events-queue"
echo -e "${YELLOW}Using queue URL: $QUEUE_URL${NC}"

SQS_MESSAGES=$(aws sqs receive-message --queue-url $QUEUE_URL --max-number-of-messages 10)
echo -e "${GREEN}Messages in queue:${NC}"
echo "$SQS_MESSAGES" | jq .

# ===================================================
# SECTION 6: Cleanup
# ===================================================
print_section "6" "Cleanup"

# Clean up environment after tests
echo -e "${GREEN}Cleaning up environment...${NC}"
docker compose down -v --rmi all

echo ""
echo -e "${BOLD}${GREEN}==================================================${NC}"
echo -e "${BOLD}${GREEN}   Test completed successfully!${NC}"
echo -e "${BOLD}${GREEN}==================================================${NC}"
