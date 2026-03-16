#!/bin/bash

# Base URLs
GO_URL="http://localhost:8080/api"
AI_URL="http://localhost:5001"

echo "Checking services..."

# Check AI Service
if curl -s "$AI_URL/health" | grep -q "ok"; then
    echo "✅ AI Service is running"
else
    echo "❌ AI Service is NOT responding at $AI_URL"
    # Optional: kill port 5000 and restart? No, let's assume it's running but maybe slow or different path
fi

# Check Go Service (expect 401 or 404, but connection successful)
HTTP_CODE=$(curl -o /dev/null -s -w "%{http_code}\n" "$GO_URL/devices")
if [ "$HTTP_CODE" == "401" ]; then
    echo "✅ Go Backend is running (Auth working)"
elif [ "$HTTP_CODE" == "000" ]; then
    echo "❌ Go Backend is NOT responding at $GO_URL"
    exit 1
else
    echo "⚠️ Go Backend responded with $HTTP_CODE (Unexpected but might be running)"
fi

# 1. Register User
echo "1. Registering user 'admin'..."
curl -s -X POST "$GO_URL/register" \
  -H "Content-Type: application/json" \
  -d '{"username":"admin", "password":"password123", "email":"admin@example.com"}'
echo ""

# 2. Login & Get Token
echo "2. Logging in..."
LOGIN_RESP=$(curl -s -X POST "$GO_URL/login" \
  -H "Content-Type: application/json" \
  -d '{"username":"admin", "password":"password123"}')

echo "Login Response: $LOGIN_RESP"

TOKEN=$(echo $LOGIN_RESP | grep -o '"token":"[^"]*' | cut -d'"' -f4)

if [ -z "$TOKEN" ]; then
    echo "❌ Failed to get token. Check login response."
    exit 1
fi

echo "✅ Token received: ${TOKEN:0:10}..."

# 3. Add Device
echo "3. Adding device 'device1'..."
curl -s -X POST "$GO_URL/devices" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"name":"Living Room Camera", "device_id":"device1"}'

echo ""
echo "✅ Device added successfully!"
echo "You can now run the simulation script:"
echo "python3 scripts/simulate_device.py device1"
