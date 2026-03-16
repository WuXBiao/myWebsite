#!/bin/bash

# Test USB device scanning and adding flow

echo "Testing USB Device Scan and Add Flow..."
echo "========================================"

# First, login to get token
echo "1. Logging in..."
LOGIN_RESPONSE=$(curl -s -X POST http://localhost:8080/api/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "testpass"
  }')

TOKEN=$(echo $LOGIN_RESPONSE | grep -o '"token":"[^"]*' | cut -d'"' -f4)

if [ -z "$TOKEN" ]; then
  echo "Failed to get token. Exiting."
  exit 1
fi

echo "✓ Logged in successfully"
echo ""

# Scan USB devices
echo "2. Scanning USB devices..."
USB_SCAN_RESPONSE=$(curl -s -X GET http://localhost:8080/api/devices/scan-usb \
  -H "Authorization: Bearer $TOKEN")

echo "USB Devices found:"
echo $USB_SCAN_RESPONSE | python3 -m json.tool 2>/dev/null | grep -E '"device_id"|"name"'
echo ""

# Extract device_id and name from scan response
DEVICE_ID=$(echo $USB_SCAN_RESPONSE | python3 -c "import sys, json; data=json.load(sys.stdin); print(data['devices'][0]['device_id'] if data['devices'] else '')" 2>/dev/null)
DEVICE_NAME=$(echo $USB_SCAN_RESPONSE | python3 -c "import sys, json; data=json.load(sys.stdin); print(data['devices'][0]['name'] if data['devices'] else '')" 2>/dev/null)

if [ -z "$DEVICE_ID" ]; then
  echo "No USB devices found. Exiting."
  exit 1
fi

echo "3. Adding device to system..."
echo "   Device ID: $DEVICE_ID"
echo "   Device Name: $DEVICE_NAME"

ADD_RESPONSE=$(curl -s -X POST http://localhost:8080/api/devices \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d "{
    \"name\": \"$DEVICE_NAME\",
    \"device_id\": \"$DEVICE_ID\"
  }")

echo "Add Response:"
echo $ADD_RESPONSE | python3 -m json.tool 2>/dev/null || echo $ADD_RESPONSE
echo ""

# Get all devices
echo "4. Getting all devices..."
DEVICES_RESPONSE=$(curl -s -X GET http://localhost:8080/api/devices \
  -H "Authorization: Bearer $TOKEN")

echo "All Devices:"
echo $DEVICES_RESPONSE | python3 -m json.tool 2>/dev/null | grep -E '"id"|"name"|"device_id"|"status"'
echo ""

echo "========================================"
echo "✓ Test completed successfully!"
