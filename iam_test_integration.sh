#!/bin/bash

# ===================================================
# Snackbar Management System - IAM Integration Test Script
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

# ===================================================
# SECTION 2: User Registration Testing
# ===================================================
print_section "2" "User Registration Testing"

# Test user registration via API with valid role (CONSUMER or ADMIN)
echo -e "${GREEN}Testing user registration with valid role (CONSUMER)...${NC}"
REGISTER_RESPONSE=$(curl -s -X POST http://localhost:8080/api/user/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "Password123!",
    "cpf": "12345678900",
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
  REGISTER_RESPONSE=$(curl -s -X POST http://localhost:8080/api/user/auth/signup \
    -H "Content-Type: application/json" \
    -d '{
      "email": "admin@example.com",
      "password": "Password123!",
      "cpf": "98765432100",
      "role": "ADMIN",
      "fullName": "Admin User"
    }')
  
  echo "API Register Response (ADMIN role): $REGISTER_RESPONSE"
  USER_ID=$(echo $REGISTER_RESPONSE | grep -o '"id":"[^"]*' | cut -d'"' -f4)
  
  if [ -z "$USER_ID" ]; then
    echo -e "${RED}Failed to register user with ADMIN role. Checking application logs...${NC}"
    # Check application logs for errors
    echo -e "${YELLOW}Checking application logs for errors...${NC}"
    docker compose logs app | grep -i "error" | tail -10
    
    # Try with a different CPF in case the user already exists
    echo -e "${YELLOW}Trying with a different CPF...${NC}"
    REGISTER_RESPONSE=$(curl -s -X POST http://localhost:8080/api/user/auth/signup \
      -H "Content-Type: application/json" \
      -d '{
        "email": "test2@example.com",
        "password": "Password123!",
        "cpf": "11122233344",
        "role": "CONSUMER",
        "fullName": "Test User 2"
      }')
    
    echo "API Register Response (different CPF): $REGISTER_RESPONSE"
    USER_ID=$(echo $REGISTER_RESPONSE | grep -o '"id":"[^"]*' | cut -d'"' -f4)
    
    if [ -z "$USER_ID" ]; then
      echo -e "${RED}Failed to register user with alternative CPF. Continuing with login tests using predefined credentials.${NC}"
      # We'll continue with login tests using predefined credentials
    else
      echo -e "${GREEN}User registered with alternative CPF. ID: $USER_ID${NC}"
    fi
  else
    echo -e "${GREEN}User registered successfully with ADMIN role. ID: $USER_ID${NC}"
  fi
else
  echo -e "${GREEN}User registered successfully with CONSUMER role. ID: $USER_ID${NC}"
fi

# Now test with an invalid role to verify error handling
echo -e "${CYAN}Testing registration with invalid role (CHEF)...${NC}"
INVALID_ROLE_RESPONSE=$(curl -s -X POST http://localhost:8080/api/user/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "email": "chef@example.com",
    "password": "Password123!",
    "cpf": "55566677788",
    "role": "CHEF",
    "fullName": "Chef User"
  }')

echo "API Register Response (invalid role): $INVALID_ROLE_RESPONSE"

# Check if the error message contains information about the invalid role
if [[ "$INVALID_ROLE_RESPONSE" == *"No enum constant"* && "$INVALID_ROLE_RESPONSE" == *"CHEF"* ]]; then
  echo -e "${GREEN}Successfully detected invalid role error${NC}"
else
  echo -e "${YELLOW}Unexpected response for invalid role test${NC}"
fi

# ===================================================
# SECTION 3: User Login Testing
# ===================================================
print_section "3" "User Login Testing"

# Test user login via API
echo -e "${GREEN}Testing user login via API...${NC}"
LOGIN_RESPONSE=$(curl -s -X POST http://localhost:8080/api/user/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "cpf": "12345678900",
    "password": "Password123!"
  }')

echo "API Login Response: $LOGIN_RESPONSE"

# Extract JWT token from response
JWT_TOKEN=$(echo $LOGIN_RESPONSE | grep -o '"token":"[^"]*' | cut -d'"' -f4)

if [ -z "$JWT_TOKEN" ]; then
  echo -e "${YELLOW}Failed to login with first user, trying with second user...${NC}"
  
  LOGIN_RESPONSE=$(curl -s -X POST http://localhost:8080/api/user/auth/login \
    -H "Content-Type: application/json" \
    -d '{
      "cpf": "98765432100",
      "password": "Password123!"
    }')
  
  echo "Second API Login Response: $LOGIN_RESPONSE"
  JWT_TOKEN=$(echo $LOGIN_RESPONSE | grep -o '"token":"[^"]*' | cut -d'"' -f4)
  
  if [ -z "$JWT_TOKEN" ]; then
    echo -e "${RED}Failed to login with second user. Testing anonymous login...${NC}"
    
    # Try anonymous login if available
    LOGIN_RESPONSE=$(curl -s -X POST http://localhost:8080/api/user/auth/login \
      -H "Content-Type: application/json" \
      -d '{
        "cpf": "anonymous",
        "password": ""
      }')
    
    echo "Anonymous Login Response: $LOGIN_RESPONSE"
    JWT_TOKEN=$(echo $LOGIN_RESPONSE | grep -o '"token":"[^"]*' | cut -d'"' -f4)
    
    if [ -z "$JWT_TOKEN" ]; then
      echo -e "${RED}Failed to login anonymously. Continuing without authentication.${NC}"
    else
      echo -e "${GREEN}Anonymous login successful! JWT Token obtained.${NC}"
    fi
  else
    echo -e "${GREEN}Login successful with second user! JWT Token obtained.${NC}"
  fi
else
  echo -e "${GREEN}Login successful! JWT Token obtained.${NC}"
fi

# ===================================================
# SECTION 4: User Retrieval Testing
# ===================================================
print_section "4" "User Retrieval Testing"

# Test getting all users
echo -e "${GREEN}Testing retrieval of all users...${NC}"
if [ -z "$JWT_TOKEN" ]; then
  GET_ALL_RESPONSE=$(curl -s -X GET http://localhost:8080/api/user/)
else
  GET_ALL_RESPONSE=$(curl -s -X GET http://localhost:8080/api/user/ \
    -H "Authorization: Bearer $JWT_TOKEN")
fi

echo "API Get All Users Response: $GET_ALL_RESPONSE"

# Check if we got a valid response
if [[ "$GET_ALL_RESPONSE" == *"id"* ]]; then
  echo -e "${GREEN}Successfully retrieved all users${NC}"
  
  # Count the number of users
  USER_COUNT=$(echo "$GET_ALL_RESPONSE" | grep -o '"id"' | wc -l)
  echo -e "${GREEN}Number of users in the system: $USER_COUNT${NC}"
else
  echo -e "${RED}Failed to retrieve all users${NC}"
fi

# Test getting user by CPF
echo -e "${GREEN}Testing retrieval of user by CPF...${NC}"
if [ -z "$JWT_TOKEN" ]; then
  GET_BY_CPF_RESPONSE=$(curl -s -X GET http://localhost:8080/api/user/cpf/12345678900)
else
  GET_BY_CPF_RESPONSE=$(curl -s -X GET http://localhost:8080/api/user/cpf/12345678900 \
    -H "Authorization: Bearer $JWT_TOKEN")
fi

echo "API Get User by CPF Response: $GET_BY_CPF_RESPONSE"

# Check if we got a valid response
if [[ "$GET_BY_CPF_RESPONSE" == *"id"* ]]; then
  echo -e "${GREEN}Successfully retrieved user by CPF${NC}"
  
  # Extract user details
  USER_NAME=$(echo "$GET_BY_CPF_RESPONSE" | grep -o '"name":"[^"]*' | cut -d'"' -f4)
  USER_ROLE=$(echo "$GET_BY_CPF_RESPONSE" | grep -o '"role":"[^"]*' | cut -d'"' -f4)
  
  echo -e "${GREEN}User name: $USER_NAME, Role: $USER_ROLE${NC}"
else
  echo -e "${YELLOW}Failed to retrieve user with first CPF, trying second CPF...${NC}"
  
  if [ -z "$JWT_TOKEN" ]; then
    GET_BY_CPF_RESPONSE=$(curl -s -X GET http://localhost:8080/api/user/cpf/98765432100)
  else
    GET_BY_CPF_RESPONSE=$(curl -s -X GET http://localhost:8080/api/user/cpf/98765432100 \
      -H "Authorization: Bearer $JWT_TOKEN")
  fi
  
  echo "API Get User by Second CPF Response: $GET_BY_CPF_RESPONSE"
  
  if [[ "$GET_BY_CPF_RESPONSE" == *"id"* ]]; then
    echo -e "${GREEN}Successfully retrieved user by second CPF${NC}"
  else
    echo -e "${RED}Failed to retrieve user by CPF${NC}"
  fi
fi

# ===================================================
# SECTION 5: User Deletion Testing
# ===================================================
print_section "5" "User Deletion Testing"

# Test user deletion via API
if [ -n "$USER_ID" ]; then
  echo -e "${GREEN}Testing user deletion via API...${NC}"
  if [ -z "$JWT_TOKEN" ]; then
    DELETE_RESPONSE=$(curl -s -X DELETE http://localhost:8080/api/user/$USER_ID)
  else
    DELETE_RESPONSE=$(curl -s -X DELETE http://localhost:8080/api/user/$USER_ID \
      -H "Authorization: Bearer $JWT_TOKEN")
  fi

  echo "API Delete Response: $DELETE_RESPONSE"

  # Verify user deletion
  echo -e "${GREEN}Verifying user deletion...${NC}"
  if [ -z "$JWT_TOKEN" ]; then
    GET_DELETED_RESPONSE=$(curl -s -X GET http://localhost:8080/api/user/cpf/12345678900)
  else
    GET_DELETED_RESPONSE=$(curl -s -X GET http://localhost:8080/api/user/cpf/12345678900 \
      -H "Authorization: Bearer $JWT_TOKEN")
  fi
  
  echo "Get Deleted User Response: $GET_DELETED_RESPONSE"

  if [[ "$GET_DELETED_RESPONSE" == *"User not found"* || "$GET_DELETED_RESPONSE" == *"error"* || "$GET_DELETED_RESPONSE" == "" ]]; then
    echo -e "${GREEN}User successfully deleted!${NC}"
  else
    echo -e "${RED}User may not have been deleted. Response: $GET_DELETED_RESPONSE${NC}"
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
INVALID_REGISTER_RESPONSE=$(curl -s -X POST http://localhost:8080/api/user/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "email": "invalid-email",
    "password": "short",
    "cpf": "invalid-cpf",
    "role": "INVALID_ROLE",
    "fullName": ""
  }')

echo "Invalid Registration Response: $INVALID_REGISTER_RESPONSE"

# Test login with invalid credentials
echo -e "${GREEN}Testing login with invalid credentials...${NC}"
INVALID_LOGIN_RESPONSE=$(curl -s -X POST http://localhost:8080/api/user/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "cpf": "nonexistent",
    "password": "wrongpassword"
  }')

echo "Invalid Login Response: $INVALID_LOGIN_RESPONSE"

# Test getting non-existent user
echo -e "${GREEN}Testing retrieval of non-existent user...${NC}"
NONEXISTENT_USER_RESPONSE=$(curl -s -X GET http://localhost:8080/api/user/cpf/00000000000)

echo "Non-existent User Response: $NONEXISTENT_USER_RESPONSE"

# ===================================================
# SECTION 7: JWT Token Validation
# ===================================================
print_section "7" "JWT Token Validation"

if [ -n "$JWT_TOKEN" ]; then
  echo -e "${GREEN}Analyzing JWT token structure...${NC}"
  
  # Split the JWT token into header, payload, and signature
  IFS='.' read -r HEADER PAYLOAD SIGNATURE <<< "$JWT_TOKEN"
  
  # Decode the header and payload
  DECODED_HEADER=$(echo $HEADER | base64 -d 2>/dev/null || echo "Failed to decode header")
  DECODED_PAYLOAD=$(echo $PAYLOAD | base64 -d 2>/dev/null || echo "Failed to decode payload")
  
  echo "JWT Header: $DECODED_HEADER"
  echo "JWT Payload: $DECODED_PAYLOAD"
  
  # Extract expiration time from payload
  EXPIRATION=$(echo $DECODED_PAYLOAD | grep -o '"exp":[0-9]*' | cut -d':' -f2)
  
  if [ -n "$EXPIRATION" ]; then
    # Convert expiration timestamp to human-readable date
    EXPIRATION_DATE=$(date -d @$EXPIRATION 2>/dev/null || echo "Failed to convert timestamp")
    echo -e "${GREEN}Token expires at: $EXPIRATION_DATE${NC}"
  else
    echo -e "${YELLOW}Could not extract expiration time from token${NC}"
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
IAM_LOGS=$(docker compose logs app | grep -i -E "auth|user|login|register|jwt|security" | tail -20)
echo "$IAM_LOGS"

# ===================================================
# SECTION 9: Cleanup
# ===================================================
print_section "9" "Cleanup"

# Clean up environment after tests
echo -e "${GREEN}Cleaning up environment...${NC}"
docker compose down -v --rmi all

echo ""
echo -e "${BOLD}${GREEN}==================================================${NC}"
echo -e "${BOLD}${GREEN}   IAM Integration Test completed!${NC}"
echo -e "${BOLD}${GREEN}==================================================${NC}"
