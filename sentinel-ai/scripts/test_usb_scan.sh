#!/bin/bash

# Test USB device scanning API

echo "Testing USB Device Scan API..."
echo "================================"

# First, login to get token
echo "1. Logging in..."
LOGIN_RESPONSE=$(curl -s -X POST http://localhost:8080/api/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "testpass"
  }')

echo "Login Response: $LOGIN_RESPONSE"

# Extract token from response
TOKEN=$(echo $LOGIN_RESPONSE | grep -o '"token":"[^"]*' | cut -d'"' -f4)

if [ -z "$TOKEN" ]; then
  echo "Failed to get token. Creating test user first..."
  
  # Register a test user
  REGISTER_RESPONSE=$(curl -s -X POST http://localhost:8080/api/register \
    -H "Content-Type: application/json" \
    -d '{
      "username": "testuser",
      "password": "testpass",
      "email": "test@example.com"
    }')
  
  echo "Register Response: $REGISTER_RESPONSE"
  
  # Try login again
  LOGIN_RESPONSE=$(curl -s -X POST http://localhost:8080/api/login \
    -H "Content-Type: application/json" \
    -d '{
      "username": "testuser",
      "password": "testpass"
    }')
  
  TOKEN=$(echo $LOGIN_RESPONSE | grep -o '"token":"[^"]*' | cut -d'"' -f4)
fi

echo "Token: $TOKEN"
echo ""

# Test USB scan endpoint
echo "2. Scanning USB devices..."
USB_SCAN_RESPONSE=$(curl -s -X GET http://localhost:8080/api/devices/scan-usb \
  -H "Authorization: Bearer $TOKEN")

echo "USB Scan Response:"
echo $USB_SCAN_RESPONSE | python3 -m json.tool 2>/dev/null || echo $USB_SCAN_RESPONSE

echo ""
echo "================================"
echo "Test completed!"
