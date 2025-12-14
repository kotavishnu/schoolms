#!/bin/bash

# Student Service API Test Script
# Tests all endpoints of the Student Service

BASE_URL="http://localhost:8081"
API_BASE="${BASE_URL}/api/v1/students"

echo "========================================"
echo "Student Service API Endpoint Tests"
echo "========================================"
echo ""

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Test counter
PASSED=0
FAILED=0

# Helper function to test endpoint
test_endpoint() {
    local name=$1
    local method=$2
    local url=$3
    local data=$4
    local expected_status=$5

    echo "Testing: $name"
    echo "URL: $method $url"

    if [ -z "$data" ]; then
        response=$(curl -s -w "\n%{http_code}" -X $method "$url" -H "Content-Type: application/json")
    else
        response=$(curl -s -w "\n%{http_code}" -X $method "$url" -H "Content-Type: application/json" -d "$data")
    fi

    http_code=$(echo "$response" | tail -n1)
    body=$(echo "$response" | sed '$d')

    if [ "$http_code" = "$expected_status" ]; then
        echo -e "${GREEN}✓ PASSED${NC} (HTTP $http_code)"
        ((PASSED++))
    else
        echo -e "${RED}✗ FAILED${NC} (Expected: $expected_status, Got: $http_code)"
        ((FAILED++))
    fi

    echo "Response: $(echo "$body" | head -c 200)..."
    echo "----------------------------------------"
    echo ""
}

# 1. Health Check
test_endpoint "Health Check" "GET" "${BASE_URL}/actuator/health" "" "200"

# 2. List Students (Empty)
test_endpoint "List All Students" "GET" "${API_BASE}" "" "200"

# 3. Create Student
CREATE_DATA='{
  "firstName": "John",
  "lastName": "Doe",
  "dateOfBirth": "2010-05-15",
  "email": "john.doe@example.com",
  "mobile": "1234567890",
  "address": "123 Main St, Springfield, IL 62701, USA",
  "fatherName": "Richard Doe",
  "motherName": "Jane Doe",
  "identificationMark": "Mole on left cheek",
  "aadhaarNumber": "123456789012"
}'
test_endpoint "Create Student" "POST" "${API_BASE}" "$CREATE_DATA" "201"

# 4. List Students (Should have 1)
test_endpoint "List Students After Create" "GET" "${API_BASE}" "" "200"

# 5. Get Student by ID (assuming ID=1)
test_endpoint "Get Student by ID" "GET" "${API_BASE}/1" "" "200"

# 6. Update Student
UPDATE_DATA='{
  "firstName": "John",
  "lastName": "Smith",
  "mobile": "9999999999"
}'
test_endpoint "Update Student" "PUT" "${API_BASE}/1" "$UPDATE_DATA" "200"

# 7. Update Status
STATUS_DATA='{
  "status": "ACTIVE"
}'
test_endpoint "Update Student Status" "PATCH" "${API_BASE}/1/status" "$STATUS_DATA" "200"

# 8. Search by Last Name
test_endpoint "Search by Last Name" "GET" "${API_BASE}?lastName=Smith" "" "200"

# 9. Get Non-Existent Student
test_endpoint "Get Non-Existent Student" "GET" "${API_BASE}/9999" "" "404"

# 10. Create Duplicate Student (same email)
test_endpoint "Create Duplicate Student" "POST" "${API_BASE}" "$CREATE_DATA" "409"

# 11. Invalid Request (missing required fields)
INVALID_DATA='{
  "firstName": "Test"
}'
test_endpoint "Create Invalid Student" "POST" "${API_BASE}" "$INVALID_DATA" "400"

# 12. Swagger UI (302 redirect is expected)
test_endpoint "Swagger UI" "GET" "${BASE_URL}/swagger-ui.html" "" "302"

# 13. API Docs
test_endpoint "OpenAPI Docs" "GET" "${BASE_URL}/v3/api-docs" "" "200"

echo "========================================"
echo "Test Summary"
echo "========================================"
echo -e "${GREEN}Passed: $PASSED${NC}"
echo -e "${RED}Failed: $FAILED${NC}"
echo "Total: $((PASSED + FAILED))"
echo "========================================"

if [ $FAILED -eq 0 ]; then
    echo -e "${GREEN}All tests passed!${NC}"
    exit 0
else
    echo -e "${RED}Some tests failed!${NC}"
    exit 1
fi
