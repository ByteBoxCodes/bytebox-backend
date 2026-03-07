#!/bin/bash

BASE_URL="http://localhost:8080/api/users"

echo "=== Testing Forgot Password Flow ==="

echo -e "\n1. Testing forgot-password with non-existent email"
curl -s -X POST $BASE_URL/forgot-password \
  -H "Content-Type: application/json" \
  -d '{"email": "does_not_exist@example.com"}' | jq

echo -e "\n\n2. Testing forgot-password with valid email (Make sure your spring boot app is running, and you have configured valid Google SMTP credentials in application.properties or using a local SMTP server like Mailhog)"
# Replace with a real email present in your database to actually receive the token
echo -e "To actually test this, run the command with a valid email that exists in your local DB:"
echo "curl -s -X POST $BASE_URL/forgot-password -H \"Content-Type: application/json\" -d '{\"email\": \"your_real_email@example.com\"}'"

echo -e "\n\n3. Testing reset-password with invalid token"
curl -s -X POST $BASE_URL/reset-password \
  -H "Content-Type: application/json" \
  -d '{"token": "invalid-token", "newPassword": "new_password"}' | jq

echo -e "\n\n=== Manual Steps Required ==="
echo "1. Replace 'your_real_email@example.com' with an actual email in your DB."
echo "2. Run the forgot password curl command."
echo "3. Check your email or MailHog inbox for the reset link."
echo "4. Copy the UUID token from the link."
echo "5. Run the reset password curl command with the valid token."
