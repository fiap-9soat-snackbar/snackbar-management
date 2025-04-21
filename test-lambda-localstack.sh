#!/bin/bash
# Script to test the product operations Lambda function in LocalStack

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[0;33m'
NC='\033[0m' # No Color

echo -e "${YELLOW}Testing Product Operations Lambda in LocalStack${NC}"

# Get the API Gateway ID
API_ID=$(docker exec localstack awslocal apigateway get-rest-apis --query "items[?name=='snackbar-product-operations-api'].id" --output text)

if [ -z "$API_ID" ]; then
  echo -e "${RED}API Gateway not found. Make sure LocalStack is running and the Lambda function is deployed.${NC}"
  exit 1
fi

# Set the API endpoint
API_URL="http://localhost:4566/restapis/$API_ID/test/_user_request_/product-operations"
echo -e "${YELLOW}Using API URL: $API_URL${NC}"

# Test CREATE operation
echo -e "\n${GREEN}Testing CREATE operation${NC}"
CREATE_RESPONSE=$(curl -s -X POST \
  -H "Content-Type: application/json" \
  -d '{
    "operation": "CREATE",
    "product": {
      "name": "Test Burger",
      "category": "Lanche",
      "description": "A delicious test burger with cheese",
      "price": 15.99,
      "cookingTime": 8
    }
  }' \
  $API_URL)

echo "Response: $CREATE_RESPONSE"

# Extract product ID from response
PRODUCT_ID=$(echo $CREATE_RESPONSE | jq -r '.body | fromjson | .data.productId // empty')

if [ -z "$PRODUCT_ID" ]; then
  echo -e "${RED}Failed to extract product ID from response${NC}"
  exit 1
fi

echo -e "${GREEN}Created product with ID: $PRODUCT_ID${NC}"

# Test UPDATE operation
echo -e "\n${GREEN}Testing UPDATE operation${NC}"
UPDATE_RESPONSE=$(curl -s -X POST \
  -H "Content-Type: application/json" \
  -d '{
    "operation": "UPDATE",
    "productId": "'$PRODUCT_ID'",
    "product": {
      "name": "Updated Test Burger",
      "category": "Lanche",
      "description": "An updated delicious test burger with extra cheese",
      "price": 17.99,
      "cookingTime": 10
    }
  }' \
  $API_URL)

echo "Response: $UPDATE_RESPONSE"

# Test DELETE operation
echo -e "\n${GREEN}Testing DELETE operation${NC}"
DELETE_RESPONSE=$(curl -s -X POST \
  -H "Content-Type: application/json" \
  -d '{
    "operation": "DELETE",
    "productId": "'$PRODUCT_ID'"
  }' \
  $API_URL)

echo "Response: $DELETE_RESPONSE"

# Check SQS messages
echo -e "\n${GREEN}Checking SQS messages${NC}"
QUEUE_URL="http://localhost:4566/000000000000/product-events"
echo -e "${YELLOW}Using queue URL: $QUEUE_URL${NC}"

# Use docker exec to run awslocal inside the LocalStack container
echo -e "\n${GREEN}Messages in queue:${NC}"
docker exec localstack awslocal sqs receive-message \
  --queue-url $QUEUE_URL \
  --max-number-of-messages 10 \
  --wait-time-seconds 1 | jq

echo -e "\n${GREEN}Test completed successfully!${NC}"
