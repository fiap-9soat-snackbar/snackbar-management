#!/bin/bash

# ===================================================
# Snackbar Management System - IAM Integration Test Script (V2)
# ===================================================
# This version tests the new clean architecture implementation
# using the temporary v2 API endpoints

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
    fi
  fi
fi

# Test registration with invalid role
echo -e "${CYAN}Testing registration with invalid role (CHEF)...${NC}"
INVALID_ROLE_RESPONSE=$(curl -s -X POST http://localhost:8080/api/v2/user/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "email": "chef@example.com",
    "password": "Password123!",
    "cpf": "11122233355",
    "role": "CHEF",
    "fullName": "Chef User"
  }')

echo "API Register Response (invalid role): $INVALID_ROLE_RESPONSE"

# Check if response contains error message
if echo "$INVALID_ROLE_RESPONSE" | grep -q "error\|invalid\|not valid"; then
  echo -e "${GREEN}Successfully detected invalid role${NC}"
else
  echo -e "${YELLOW}Unexpected response for invalid role test${NC}"
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
    "cpf": "12345678900",
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
      "cpf": "52998224725",
      "password": "Password123!"
    }')
  
  echo "Second API Login Response: $LOGIN_RESPONSE"
  
  # Extract token from response
  TOKEN=$(echo $LOGIN_RESPONSE | grep -o '"token":"[^"]*' | cut -d'"' -f4)
  
  if [ -z "$TOKEN" ]; then
    echo -e "${RED}Failed to login with second user. Testing anonymous login...${NC}"
    
    # Try anonymous login
    ANON_LOGIN_RESPONSE=$(curl -s -X POST http://localhost:8080/api/v2/user/auth/login \
      -H "Content-Type: application/json" \
      -d '{
        "cpf": "",
        "password": "",
        "anonymous": true
      }')
    
    echo "Anonymous Login Response: $ANON_LOGIN_RESPONSE"
    
    # Extract token from response
    TOKEN=$(echo $ANON_LOGIN_RESPONSE | grep -o '"token":"[^"]*' | cut -d'"' -f4)
    
    if [ -z "$TOKEN" ]; then
      echo -e "${RED}Failed to login anonymously. Continuing without authentication.${NC}"
    else
      echo -e "${GREEN}Successfully logged in anonymously${NC}"
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
else
  echo -e "${RED}Failed to retrieve all users${NC}"
fi

# Test retrieval of user by CPF
echo -e "${GREEN}Testing retrieval of user by CPF...${NC}"
USER_BY_CPF_RESPONSE=$(curl -s -X GET http://localhost:8080/api/v2/user/cpf/12345678900 \
  -H "Authorization: Bearer $TOKEN")

echo "API Get User by CPF Response: $USER_BY_CPF_RESPONSE"

# Check if response contains user
if echo "$USER_BY_CPF_RESPONSE" | grep -q "id"; then
  echo -e "${GREEN}Successfully retrieved user by CPF${NC}"
else
  echo -e "${YELLOW}Failed to retrieve user with first CPF, trying second CPF...${NC}"
  
  # Try with second CPF
  USER_BY_CPF_RESPONSE=$(curl -s -X GET http://localhost:8080/api/v2/user/cpf/52998224725 \
    -H "Authorization: Bearer $TOKEN")
  
  echo "API Get User by Second CPF Response: $USER_BY_CPF_RESPONSE"
  
  # Check if response contains user
  if echo "$USER_BY_CPF_RESPONSE" | grep -q "id"; then
    echo -e "${GREEN}Successfully retrieved user by second CPF${NC}"
  else
    echo -e "${RED}Failed to retrieve user by CPF${NC}"
  fi
fi

# ===================================================
# SECTION 5: User Deletion Testing
# ===================================================
print_section "5" "User Deletion Testing"

# Test user deletion
if [ -n "$USER_ID" ]; then
  echo -e "${GREEN}Testing user deletion...${NC}"
  DELETE_RESPONSE=$(curl -s -X DELETE http://localhost:8080/api/v2/user/$USER_ID \
    -H "Authorization: Bearer $TOKEN")
  
  echo "API Delete User Response: $DELETE_RESPONSE"
  
  # Check if deletion was successful
  if [ -z "$DELETE_RESPONSE" ] || echo "$DELETE_RESPONSE" | grep -q "success\|deleted"; then
    echo -e "${GREEN}Successfully deleted user${NC}"
  else
    echo -e "${RED}Failed to delete user${NC}"
  fi
else
  echo -e "${YELLOW}Skipping user deletion test as no user ID was obtained${NC}"
fi

# ===================================================
# SECTION 6: Error Handling Testing
# ===================================================
print_section "6" "Error Handling Testing"

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

# Test retrieval of non-existent user
echo -e "${GREEN}Testing retrieval of non-existent user...${NC}"
NONEXISTENT_USER_RESPONSE=$(curl -s -X GET http://localhost:8080/api/v2/user/cpf/99999999999 \
  -H "Authorization: Bearer $TOKEN")

echo "Non-existent User Response: $NONEXISTENT_USER_RESPONSE"

# ===================================================
# SECTION 7: JWT Token Validation
# ===================================================
print_section "7" "JWT Token Validation"

if [ -n "$TOKEN" ]; then
  echo -e "${GREEN}Testing JWT token validation...${NC}"
  
  # Test with valid token
  VALID_TOKEN_RESPONSE=$(curl -s -X GET http://localhost:8080/api/v2/user/ \
    -H "Authorization: Bearer $TOKEN")
  
  echo "Valid Token Response: $VALID_TOKEN_RESPONSE"
  
  # Test with invalid token
  INVALID_TOKEN_RESPONSE=$(curl -s -X GET http://localhost:8080/api/v2/user/ \
    -H "Authorization: Bearer ${TOKEN}invalid")
  
  echo "Invalid Token Response: $INVALID_TOKEN_RESPONSE"
  
  # Check if responses differ
  if [ "$VALID_TOKEN_RESPONSE" != "$INVALID_TOKEN_RESPONSE" ]; then
    echo -e "${GREEN}JWT token validation is working correctly${NC}"
  else
    echo -e "${RED}JWT token validation might not be working correctly${NC}"
  fi
else
  echo -e "${YELLOW}Skipping JWT token validation as no token was obtained${NC}"
fi

# ===================================================
# SECTION 8: Application Logs Analysis
# ===================================================
print_section "8" "Application Logs Analysis"

# Check application logs for IAM-related entries
echo -e "${GREEN}Checking application logs for IAM-related entries...${NC}"
docker compose logs app | grep -i "iam\|user\|auth\|jwt\|token\|login\|register" | grep -i "error\|exception\|warn" | tail -n 20

# ===================================================
# SECTION 9: Cleanup
# ===================================================
print_section "9" "Cleanup"

# Clean up environment
echo -e "${GREEN}Cleaning up environment...${NC}"
docker compose down -v --rmi all

echo ""
echo -e "${BOLD}${GREEN}==================================================${NC}"
echo -e "${BOLD}${GREEN}   IAM Integration Test V2 completed!${NC}"
echo -e "${BOLD}${GREEN}==================================================${NC}"
