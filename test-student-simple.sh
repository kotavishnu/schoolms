#!/bin/bash

# Simple Student Service API Test
echo "==================================="
echo "Student Service API - Simple Test"
echo "==================================="
echo ""

BASE_URL="http://localhost:8081"

# 1. Health Check
echo "1. Testing Health Check..."
curl -s http://localhost:8081/actuator/health | head -c 200
echo -e "\n"

# 2. List Students (Empty)
echo "2. Listing all students (should be empty)..."
curl -s http://localhost:8081/api/v1/students | jq '.'
echo ""

# 3. Create Student
echo "3. Creating a new student..."
CREATE_RESPONSE=$(curl -s -w "\n%{http_code}" -X POST http://localhost:8081/api/v1/students \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe",
    "dateOfBirth": "2010-05-15",
    "email": "john.doe@example.com",
    "mobile": "1234567890",
    "address": "123 Main St, Springfield, IL",
    "fatherName": "Richard Doe",
    "motherName": "Jane Doe"
  }')

HTTP_CODE=$(echo "$CREATE_RESPONSE" | tail -n1)
STUDENT=$(echo "$CREATE_RESPONSE" | sed '$d')

if [ "$HTTP_CODE" = "201" ]; then
    echo "✓ Student created successfully"
    echo "$STUDENT" | jq '.'

    # Extract student ID and version
    STUDENT_ID=$(echo "$STUDENT" | jq -r '.id')
    STUDENT_VERSION=$(echo "$STUDENT" | jq -r '.version')

    echo "Student ID: $STUDENT_ID"
    echo "Version: $STUDENT_VERSION"
else
    echo "✗ Failed to create student (HTTP $HTTP_CODE)"
    echo "$STUDENT" | jq '.'
fi
echo ""

# 4. Get Student by ID (if created)
if [ ! -z "$STUDENT_ID" ]; then
    echo "4. Getting student by ID..."
    curl -s http://localhost:8081/api/v1/students/$STUDENT_ID | jq '.'
    echo ""

    # 5. Update Student
    echo "5. Updating student..."
    UPDATE_RESPONSE=$(curl -s -w "\n%{http_code}" -X PUT http://localhost:8081/api/v1/students/$STUDENT_ID \
      -H "Content-Type: application/json" \
      -d "{
        \"firstName\": \"John\",
        \"lastName\": \"Smith\",
        \"mobile\": \"9999999999\",
        \"version\": $STUDENT_VERSION
      }")

    HTTP_CODE=$(echo "$UPDATE_RESPONSE" | tail -n1)
    UPDATED=$(echo "$UPDATE_RESPONSE" | sed '$d')

    if [ "$HTTP_CODE" = "200" ]; then
        echo "✓ Student updated successfully"
        echo "$UPDATED" | jq '.'

        # Update version for next operation
        STUDENT_VERSION=$(echo "$UPDATED" | jq -r '.version')
    else
        echo "✗ Failed to update student (HTTP $HTTP_CODE)"
        echo "$UPDATED" | jq '.'
    fi
    echo ""

    # 6. Update Status
    echo "6. Updating student status..."
    STATUS_RESPONSE=$(curl -s -w "\n%{http_code}" -X PATCH http://localhost:8081/api/v1/students/$STUDENT_ID/status \
      -H "Content-Type: application/json" \
      -d "{
        \"status\": \"ACTIVE\",
        \"version\": $STUDENT_VERSION
      }")

    HTTP_CODE=$(echo "$STATUS_RESPONSE" | tail -n1)
    STATUS_UPDATED=$(echo "$STATUS_RESPONSE" | sed '$d')

    if [ "$HTTP_CODE" = "200" ]; then
        echo "✓ Status updated successfully"
        echo "$STATUS_UPDATED" | jq '.'
    else
        echo "✗ Failed to update status (HTTP $HTTP_CODE)"
        echo "$STATUS_UPDATED" | jq '.'
    fi
    echo ""
fi

# 7. List all students again
echo "7. Listing all students after operations..."
curl -s http://localhost:8081/api/v1/students | jq '.'
echo ""

# 8. Search by last name
echo "8. Searching by last name 'Smith'..."
curl -s "http://localhost:8081/api/v1/students?lastName=Smith" | jq '.'
echo ""

# 9. Swagger UI Check
echo "9. Checking Swagger UI..."
SWAGGER_CODE=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8081/swagger-ui.html)
if [ "$SWAGGER_CODE" = "302" ] || [ "$SWAGGER_CODE" = "200" ]; then
    echo "✓ Swagger UI is accessible (HTTP $SWAGGER_CODE)"
else
    echo "✗ Swagger UI not accessible (HTTP $SWAGGER_CODE)"
fi
echo ""

# 10. OpenAPI Docs Check
echo "10. Checking OpenAPI Docs..."
API_DOCS_CODE=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8081/v3/api-docs)
if [ "$API_DOCS_CODE" = "200" ]; then
    echo "✓ OpenAPI docs are accessible"
    echo "Sample:"
    curl -s http://localhost:8081/v3/api-docs | jq '.info'
else
    echo "✗ OpenAPI docs not accessible (HTTP $API_DOCS_CODE)"
fi

echo ""
echo "==================================="
echo "Test completed"
echo "==================================="
