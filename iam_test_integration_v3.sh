#!/bin/bash

# ===================================================
# Snackbar Management System - IAM Integration Test Script (V3)
# ===================================================
# This version tests the clean architecture implementation
# including the new user update functionality

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

# ===================================================
# SECTION 2: User Registration Testing
# ===================================================
print_section "2" "User Registration Testing"

# Test user registration via API with valid role (CONSUMER or ADMIN)
echo -e "${GREEN}Testing user registration with valid role (CONSUMER)...${NC}"
REGISTER_RESPONSE=$(curl -s -X POST http://localhost:8080/api/v2/user/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "Password123!",
    "cpf": "52998224725",
    "role": "CONSUMER",
    "fullName": "Test User"
  }')

echo "API Register Response (valid role): $REGISTER_RESPONSE"

# Extract user ID from response
USER_ID=$(echo $REGISTER_RESPONSE | grep -o '"id":"[^"]*' | cut -d'"' -f4)

if [ -z "$USER_ID" ]; then
  echo -e "${RED}Failed to register user with CONSUMER role${NC}"
  
  # Try with ADMIN role
  echo -e "${YELLOW}Trying with ADMIN role...${NC}"
  REGISTER_RESPONSE=$(curl -s -X POST http://localhost:8080/api/v2/user/auth/signup \
    -H "Content-Type: application/json" \
    -d '{
      "email": "admin@example.com",
      "password": "Password123!",
      "cpf": "98765432100",
      "role": "ADMIN",
      "fullName": "Admin User"
    }')
  
  echo "API Register Response (ADMIN role): $REGISTER_RESPONSE"
  
  # Extract user ID from response
  USER_ID=$(echo $REGISTER_RESPONSE | grep -o '"id":"[^"]*' | cut -d'"' -f4)
  
  if [ -z "$USER_ID" ]; then
    echo -e "${RED}Failed to register user with ADMIN role. Checking application logs...${NC}"
    
    # Check application logs for errors
    echo -e "${YELLOW}Checking application logs for errors...${NC}"
    docker compose logs app | grep -i "error\|exception" | tail -n 10
    
    # Try with a different CPF
    echo -e "${YELLOW}Trying with a different CPF...${NC}"
    REGISTER_RESPONSE=$(curl -s -X POST http://localhost:8080/api/v2/user/auth/signup \
      -H "Content-Type: application/json" \
      -d '{
        "email": "user2@example.com",
        "password": "Password123!",
        "cpf": "45474881622",
        "role": "CONSUMER",
        "fullName": "Another User"
      }')
    
    echo "API Register Response (different CPF): $REGISTER_RESPONSE"
    
    # Extract user ID from response
    USER_ID=$(echo $REGISTER_RESPONSE | grep -o '"id":"[^"]*' | cut -d'"' -f4)
    
    if [ -z "$USER_ID" ]; then
      echo -e "${RED}Failed to register user with alternative CPF. Continuing with login tests using predefined credentials.${NC}"
    else
      echo -e "${GREEN}Successfully registered user with alternative CPF. User ID: $USER_ID${NC}"
    fi
  else
    echo -e "${GREEN}Successfully registered user with ADMIN role. User ID: $USER_ID${NC}"
  fi
else
  echo -e "${GREEN}Successfully registered user with CONSUMER role. User ID: $USER_ID${NC}"
fi

# ===================================================
# SECTION 3: User Login Testing
# ===================================================
print_section "3" "User Login Testing"

# Test user login via API
echo -e "${GREEN}Testing user login via API...${NC}"
LOGIN_RESPONSE=$(curl -s -X POST http://localhost:8080/api/v2/user/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "cpf": "52998224725",
    "password": "Password123!"
  }')

echo "API Login Response: $LOGIN_RESPONSE"

# Extract token from response
TOKEN=$(echo $LOGIN_RESPONSE | grep -o '"token":"[^"]*' | cut -d'"' -f4)

if [ -z "$TOKEN" ]; then
  echo -e "${YELLOW}Failed to login with first user, trying with second user...${NC}"
  
  # Try with second user
  LOGIN_RESPONSE=$(curl -s -X POST http://localhost:8080/api/v2/user/auth/login \
    -H "Content-Type: application/json" \
    -d '{
      "cpf": "98765432100",
      "password": "Password123!"
    }')
  
  echo "Second API Login Response: $LOGIN_RESPONSE"
  
  # Extract token from response
  TOKEN=$(echo $LOGIN_RESPONSE | grep -o '"token":"[^"]*' | cut -d'"' -f4)
  
  if [ -z "$TOKEN" ]; then
    echo -e "${YELLOW}Failed to login with second user, trying with third user...${NC}"
    
    # Try with third user
    LOGIN_RESPONSE=$(curl -s -X POST http://localhost:8080/api/v2/user/auth/login \
      -H "Content-Type: application/json" \
      -d '{
        "cpf": "45474881622",
        "password": "Password123!"
      }')
    
    echo "Third API Login Response: $LOGIN_RESPONSE"
    
    # Extract token from response
    TOKEN=$(echo $LOGIN_RESPONSE | grep -o '"token":"[^"]*' | cut -d'"' -f4)
    
    if [ -z "$TOKEN" ]; then
      echo -e "${RED}Failed to login with any user. Continuing without authentication.${NC}"
    else
      echo -e "${GREEN}Successfully logged in with third user${NC}"
    fi
  else
    echo -e "${GREEN}Successfully logged in with second user${NC}"
  fi
else
  echo -e "${GREEN}Successfully logged in with first user${NC}"
fi

# ===================================================
# SECTION 4: User Retrieval Testing
# ===================================================
print_section "4" "User Retrieval Testing"

# Test retrieval of all users
echo -e "${GREEN}Testing retrieval of all users...${NC}"
ALL_USERS_RESPONSE=$(curl -s -X GET http://localhost:8080/api/v2/user/ \
  -H "Authorization: Bearer $TOKEN")

echo "API Get All Users Response: $ALL_USERS_RESPONSE"

# Check if response contains users
if echo "$ALL_USERS_RESPONSE" | grep -q "id"; then
  echo -e "${GREEN}Successfully retrieved all users${NC}"
  
  # Count the number of users
  USER_COUNT=$(echo "$ALL_USERS_RESPONSE" | grep -o '"id"' | wc -l)
  echo -e "${GREEN}Number of users in the system: $USER_COUNT${NC}"
else
  echo -e "${RED}Failed to retrieve all users${NC}"
fi

# Test retrieval of user by CPF
echo -e "${GREEN}Testing retrieval of user by CPF...${NC}"
USER_BY_CPF_RESPONSE=$(curl -s -X GET http://localhost:8080/api/v2/user/cpf/52998224725 \
  -H "Authorization: Bearer $TOKEN")

echo "API Get User by CPF Response: $USER_BY_CPF_RESPONSE"

# Check if response contains user
if echo "$USER_BY_CPF_RESPONSE" | grep -q "id"; then
  echo -e "${GREEN}Successfully retrieved user by CPF${NC}"
  
  # Extract user details for later use
  USER_EMAIL=$(echo "$USER_BY_CPF_RESPONSE" | grep -o '"email":"[^"]*' | cut -d'"' -f4)
  USER_NAME=$(echo "$USER_BY_CPF_RESPONSE" | grep -o '"name":"[^"]*' | cut -d'"' -f4)
  USER_ID_FROM_CPF=$(echo "$USER_BY_CPF_RESPONSE" | grep -o '"id":"[^"]*' | cut -d'"' -f4)
  
  echo -e "${GREEN}User details - ID: $USER_ID_FROM_CPF, Name: $USER_NAME, Email: $USER_EMAIL${NC}"
else
  echo -e "${YELLOW}Failed to retrieve user with first CPF, trying second CPF...${NC}"
  
  # Try with second CPF
  USER_BY_CPF_RESPONSE=$(curl -s -X GET http://localhost:8080/api/v2/user/cpf/98765432100 \
    -H "Authorization: Bearer $TOKEN")
  
  echo "API Get User by Second CPF Response: $USER_BY_CPF_RESPONSE"
  
  # Check if response contains user
  if echo "$USER_BY_CPF_RESPONSE" | grep -q "id"; then
    echo -e "${GREEN}Successfully retrieved user by second CPF${NC}"
    
    # Extract user details for later use
    USER_EMAIL=$(echo "$USER_BY_CPF_RESPONSE" | grep -o '"email":"[^"]*' | cut -d'"' -f4)
    USER_NAME=$(echo "$USER_BY_CPF_RESPONSE" | grep -o '"name":"[^"]*' | cut -d'"' -f4)
    USER_ID_FROM_CPF=$(echo "$USER_BY_CPF_RESPONSE" | grep -o '"id":"[^"]*' | cut -d'"' -f4)
    
    echo -e "${GREEN}User details - ID: $USER_ID_FROM_CPF, Name: $USER_NAME, Email: $USER_EMAIL${NC}"
  else
    echo -e "${RED}Failed to retrieve user by CPF${NC}"
  fi
fi

# ===================================================
# SECTION 5: User Update Testing (NEW)
# ===================================================
print_section "5" "User Update Testing"

# Use the user ID from registration or retrieval
UPDATE_USER_ID=${USER_ID:-$USER_ID_FROM_CPF}

if [ -n "$UPDATE_USER_ID" ]; then
  echo -e "${GREEN}Testing user update...${NC}"
  
  # Update user information
  UPDATE_RESPONSE=$(curl -s -X PUT http://localhost:8080/api/v2/user/$UPDATE_USER_ID \
    -H "Content-Type: application/json" \
    -H "Authorization: Bearer $TOKEN" \
    -d '{
      "name": "Updated User Name",
      "email": "updated@example.com",
      "cpf": "52998224725",
      "role": "CONSUMER",
      "password": "NewPassword123!"
    }')
  
  echo "API Update User Response: $UPDATE_RESPONSE"
  
  # Check if update was successful
  if echo "$UPDATE_RESPONSE" | grep -q "id"; then
    echo -e "${GREEN}Successfully updated user${NC}"
    
    # Extract updated user details
    UPDATED_NAME=$(echo "$UPDATE_RESPONSE" | grep -o '"name":"[^"]*' | cut -d'"' -f4)
    UPDATED_EMAIL=$(echo "$UPDATE_RESPONSE" | grep -o '"email":"[^"]*' | cut -d'"' -f4)
    
    echo -e "${GREEN}Updated user details - Name: $UPDATED_NAME, Email: $UPDATED_EMAIL${NC}"
    
    # Verify update by retrieving user again
    echo -e "${GREEN}Verifying update by retrieving user...${NC}"
    VERIFY_UPDATE_RESPONSE=$(curl -s -X GET http://localhost:8080/api/v2/user/$UPDATE_USER_ID \
      -H "Authorization: Bearer $TOKEN")
    
    echo "API Get Updated User Response: $VERIFY_UPDATE_RESPONSE"
    
    # Check if retrieved user has updated information
    if echo "$VERIFY_UPDATE_RESPONSE" | grep -q "Updated User Name"; then
      echo -e "${GREEN}Update verification successful${NC}"
    else
      echo -e "${YELLOW}Update verification failed or endpoint not available${NC}"
    fi
    
    # Test login with updated credentials
    echo -e "${GREEN}Testing login with updated credentials...${NC}"
    UPDATED_LOGIN_RESPONSE=$(curl -s -X POST http://localhost:8080/api/v2/user/auth/login \
      -H "Content-Type: application/json" \
      -d '{
        "cpf": "52998224725",
        "password": "NewPassword123!"
      }')
    
    echo "API Login Response with Updated Credentials: $UPDATED_LOGIN_RESPONSE"
    
    # Check if login was successful
    if echo "$UPDATED_LOGIN_RESPONSE" | grep -q "token"; then
      echo -e "${GREEN}Successfully logged in with updated credentials${NC}"
    else
      echo -e "${YELLOW}Failed to login with updated credentials${NC}"
    fi
  else
    echo -e "${RED}Failed to update user${NC}"
  fi
else
  echo -e "${YELLOW}Skipping user update test as no user ID was obtained${NC}"
fi

# ===================================================
# SECTION 6: User Deletion Testing
# ===================================================
print_section "6" "User Deletion Testing"

# Use the user ID from registration or retrieval
DELETE_USER_ID=${USER_ID:-$USER_ID_FROM_CPF}

if [ -n "$DELETE_USER_ID" ]; then
  echo -e "${GREEN}Testing user deletion...${NC}"
  DELETE_RESPONSE=$(curl -s -X DELETE http://localhost:8080/api/v2/user/$DELETE_USER_ID \
    -H "Authorization: Bearer $TOKEN" -w "%{http_code}" -o /dev/null)
  
  echo "API Delete User Response Code: $DELETE_RESPONSE"
  
  # Check if deletion was successful (204 No Content)
  if [ "$DELETE_RESPONSE" = "204" ]; then
    echo -e "${GREEN}Successfully deleted user${NC}"
    
    # Verify deletion by trying to retrieve the deleted user
    echo -e "${GREEN}Verifying deletion by trying to retrieve deleted user...${NC}"
    VERIFY_DELETE_RESPONSE=$(curl -s -X GET http://localhost:8080/api/v2/user/cpf/52998224725 \
      -H "Authorization: Bearer $TOKEN" -w "%{http_code}" -o /dev/null)
    
    echo "API Get Deleted User Response Code: $VERIFY_DELETE_RESPONSE"
    
    # Check if user was not found (404 Not Found)
    if [ "$VERIFY_DELETE_RESPONSE" = "404" ]; then
      echo -e "${GREEN}Deletion verification successful${NC}"
    else
      echo -e "${YELLOW}Deletion verification failed${NC}"
    fi
  else
    echo -e "${RED}Failed to delete user${NC}"
  fi
else
  echo -e "${YELLOW}Skipping user deletion test as no user ID was obtained${NC}"
fi

# ===================================================
# SECTION 7: Error Handling Testing
# ===================================================
print_section "7" "Error Handling Testing"

# Test registration with invalid data
echo -e "${GREEN}Testing registration with invalid data...${NC}"
INVALID_REG_RESPONSE=$(curl -s -X POST http://localhost:8080/api/v2/user/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "email": "invalid-email",
    "password": "short",
    "cpf": "invalid-cpf",
    "role": "CONSUMER",
    "fullName": ""
  }')

echo "Invalid Registration Response: $INVALID_REG_RESPONSE"

# Test login with invalid credentials
echo -e "${GREEN}Testing login with invalid credentials...${NC}"
INVALID_LOGIN_RESPONSE=$(curl -s -X POST http://localhost:8080/api/v2/user/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "cpf": "nonexistent",
    "password": "wrongpassword"
  }')

echo "Invalid Login Response: $INVALID_LOGIN_RESPONSE"

# Test update with invalid data
echo -e "${GREEN}Testing update with invalid data...${NC}"
if [ -n "$UPDATE_USER_ID" ]; then
  INVALID_UPDATE_RESPONSE=$(curl -s -X PUT http://localhost:8080/api/v2/user/$UPDATE_USER_ID \
    -H "Content-Type: application/json" \
    -H "Authorization: Bearer $TOKEN" \
    -d '{
      "name": "",
      "email": "invalid-email"
    }')
  
  echo "Invalid Update Response: $INVALID_UPDATE_RESPONSE"
else
  echo -e "${YELLOW}Skipping invalid update test as no user ID was obtained${NC}"
fi

# ===================================================
# SECTION 8: JWT Token Validation
# ===================================================
print_section "8" "JWT Token Validation"

if [ -n "$TOKEN" ]; then
  echo -e "${GREEN}Testing JWT token validation...${NC}"
  
  # Test with valid token
  VALID_TOKEN_RESPONSE=$(curl -s -X GET http://localhost:8080/api/v2/user/ \
    -H "Authorization: Bearer $TOKEN" -w "%{http_code}" -o /dev/null)
  
  echo "Valid Token Response Code: $VALID_TOKEN_RESPONSE"
  
  # Test with invalid token
  INVALID_TOKEN_RESPONSE=$(curl -s -X GET http://localhost:8080/api/v2/user/ \
    -H "Authorization: Bearer ${TOKEN}invalid" -w "%{http_code}" -o /dev/null)
  
  echo "Invalid Token Response Code: $INVALID_TOKEN_RESPONSE"
  
  # Check if responses differ
  if [ "$VALID_TOKEN_RESPONSE" != "$INVALID_TOKEN_RESPONSE" ]; then
    echo -e "${GREEN}JWT token validation is working correctly${NC}"
  else
    echo -e "${RED}JWT token validation might not be working correctly${NC}"
  fi
  
  # Analyze JWT token structure
  echo -e "${GREEN}Analyzing JWT token structure...${NC}"
  
  # Split the JWT token into header, payload, and signature
  IFS='.' read -r HEADER PAYLOAD SIGNATURE <<< "$TOKEN"
  
  # Decode the header and payload
  DECODED_HEADER=$(echo $HEADER | base64 -d 2>/dev/null || echo "Failed to decode header")
  DECODED_PAYLOAD=$(echo $PAYLOAD | base64 -d 2>/dev/null || echo "Failed to decode payload")
  
  echo "JWT Header: $DECODED_HEADER"
  echo "JWT Payload: $DECODED_PAYLOAD"
else
  echo -e "${YELLOW}Skipping JWT token validation as no token was obtained${NC}"
fi

# ===================================================
# SECTION 9: Event Publishing Testing
# ===================================================
print_section "9" "Event Publishing Testing"

# Check application logs for event publishing
echo -e "${GREEN}Checking application logs for event publishing...${NC}"
EVENT_LOGS=$(docker compose logs app | grep -i "event\|publish" | tail -n 20)
echo "$EVENT_LOGS"

# Check for specific event types
echo -e "${GREEN}Checking for specific event types...${NC}"
USER_CREATED_EVENTS=$(docker compose logs app | grep -i "UserCreatedEvent" | wc -l)
USER_UPDATED_EVENTS=$(docker compose logs app | grep -i "UserUpdatedEvent" | wc -l)
USER_DELETED_EVENTS=$(docker compose logs app | grep -i "UserDeletedEvent" | wc -l)

echo -e "${GREEN}Event counts:${NC}"
echo -e "${GREEN}- UserCreatedEvent: $USER_CREATED_EVENTS${NC}"
echo -e "${GREEN}- UserUpdatedEvent: $USER_UPDATED_EVENTS${NC}"
echo -e "${GREEN}- UserDeletedEvent: $USER_DELETED_EVENTS${NC}"

# ===================================================
# SECTION 10: Application Logs Analysis
# ===================================================
print_section "10" "Application Logs Analysis"

# Check application logs for IAM-related entries
echo -e "${GREEN}Checking application logs for IAM-related entries...${NC}"
docker compose logs app | grep -i "iam\|user\|auth\|jwt\|token\|login\|register" | grep -i "error\|exception\|warn" | tail -n 20

# ===================================================
# SECTION 11: Cleanup
# ===================================================
print_section "11" "Cleanup"

# Clean up environment
echo -e "${GREEN}Cleaning up environment...${NC}"
docker compose down -v --rmi all

echo ""
echo -e "${BOLD}${GREEN}==================================================${NC}"
echo -e "${BOLD}${GREEN}   IAM Integration Test V3 completed!${NC}"
echo -e "${BOLD}${GREEN}==================================================${NC}"
