#!/usr/bin/env python3
"""
Test script for image search and Baidu Baike features
"""

import requests
import json

BASE_URL = "http://localhost:5001"

def test_search_object():
    """Test searching for object information"""
    print("\n=== Testing Search Object Feature ===")
    
    test_objects = ["dog", "cat", "car", "person"]
    
    for obj in test_objects:
        try:
            response = requests.post(
                f"{BASE_URL}/search-object",
                json={"object_name": obj},
                timeout=10
            )
            
            if response.status_code == 200:
                data = response.json()
                print(f"\n✓ Search for '{obj}':")
                if data.get("baike"):
                    baike = data["baike"]
                    print(f"  Title: {baike.get('title', 'N/A')}")
                    print(f"  Description: {baike.get('description', 'N/A')[:100]}...")
                    print(f"  URL: {baike.get('url', 'N/A')}")
                else:
                    print(f"  No information found")
            else:
                print(f"✗ Search for '{obj}' failed: {response.status_code}")
                
        except Exception as e:
            print(f"✗ Error searching for '{obj}': {e}")

def test_search_image_source():
    """Test searching for image source"""
    print("\n=== Testing Search Image Source Feature ===")
    
    # Create a simple test image
    import cv2
    import numpy as np
    
    # Create a simple test image
    img = np.zeros((100, 100, 3), dtype=np.uint8)
    img[:, :] = [0, 255, 0]  # Green image
    
    # Save as JPEG
    ret, buffer = cv2.imencode('.jpg', img)
    
    try:
        files = {'image': ('test.jpg', buffer.tobytes(), 'image/jpeg')}
        response = requests.post(
            f"{BASE_URL}/search-image-source",
            files=files,
            timeout=10
        )
        
        if response.status_code == 200:
            data = response.json()
            print("✓ Image source search successful:")
            if data.get("source"):
                source = data["source"]
                print(f"  Source: {source.get('source', 'N/A')}")
                print(f"  Search URL: {source.get('search_url', 'N/A')}")
                print(f"  Note: {source.get('note', 'N/A')}")
        else:
            print(f"✗ Image source search failed: {response.status_code}")
            
    except Exception as e:
        print(f"✗ Error searching image source: {e}")

def main():
    print("Starting Search Feature Tests...")
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
    test_search_object()
    test_search_image_source()
    
    print("\n=== Test Complete ===")

if __name__ == "__main__":
    main()
