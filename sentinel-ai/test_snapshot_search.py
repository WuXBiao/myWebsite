083683#!/usr/bin/env python3
"""
Test script for snapshot search feature
Tests the new image analysis and object detection with Baike info
"""

import requests
import cv2
import numpy as np

BASE_URL = "http://localhost:5001"

def create_test_image():
    """Create a simple test image"""
    img = np.zeros((480, 640, 3), dtype=np.uint8)
    # Create a simple colored rectangle to simulate an object
    cv2.rectangle(img, (100, 100), (300, 300), (0, 255, 0), -1)
    cv2.putText(img, "Test Image", (150, 250), cv2.FONT_HERSHEY_SIMPLEX, 1, (255, 255, 255), 2)
    
    ret, buffer = cv2.imencode('.jpg', img)
    return buffer.tobytes()

def test_image_analysis():
    """Test the new image analysis feature"""
    print("\n=== Testing Image Analysis Feature ===")
    
    image_data = create_test_image()
    
    try:
        files = {'image': ('test.jpg', image_data, 'image/jpeg')}
        response = requests.post(
            f"{BASE_URL}/search-image-source",
            files=files,
            timeout=30
        )
        
        if response.status_code == 200:
            data = response.json()
            print("✓ Image analysis successful:")
            
            if data.get("data"):
                result = data["data"]
                
                # Display detected objects
                if result.get("objects"):
                    print(f"\n  Detected Objects ({len(result['objects'])}):")
                    for obj in result["objects"]:
                        print(f"    - {obj['name']} ({obj['english_name']})")
                        print(f"      Confidence: {obj['confidence']*100:.1f}%")
                        if obj.get("baike"):
                            print(f"      Baike Title: {obj['baike']['title']}")
                            print(f"      Baike URL: {obj['baike']['url']}")
                
                # Display source info
                if result.get("source"):
                    print(f"\n  Image Source Info:")
                    print(f"    Status: {result['source']['message']}")
                    print(f"    Note: {result['source']['note']}")
        else:
            print(f"✗ Image analysis failed: {response.status_code}")
            print(f"  Response: {response.text}")
            
    except Exception as e:
        print(f"✗ Error analyzing image: {e}")

def main():
    print("Starting Snapshot Search Feature Tests...")
    print(f"Base URL: {BASE_URL}")
    
    try:
        # Test connection
        response = requests.get(f"{BASE_URL}/health", timeout=5)
        if response.status_code == 200:
            print("✓ AI Service is running")
        else:
            print("✗ AI Service is not responding correctly")
            return
    except Exception as e:
        print(f"✗ Cannot connect to AI Service: {e}")
        return
    
    # Run tests
    test_image_analysis()
    
    print("\n=== Test Complete ===")

if __name__ == "__main__":
    main()
