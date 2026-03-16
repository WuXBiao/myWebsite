import requests
import socket
import sys
import time
from datetime import datetime

def get_device_id():
    """Get device ID from command line or use default"""
    if len(sys.argv) > 1:
        return sys.argv[1]
    return f"device_{socket.gethostname()}"

def get_device_name():
    """Get device name from command line or use default"""
    if len(sys.argv) > 2:
        return sys.argv[2]
    return f"Device {socket.gethostname()}"

def get_local_ip():
    """Get local IP address"""
    try:
        s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        s.connect(("8.8.8.8", 80))
        ip = s.getsockname()[0]
        s.close()
        return ip
    except:
        return "127.0.0.1"

def register_device(server_url, device_id, device_name, ip_address):
    """Register device with server"""
    try:
        payload = {
            "device_id": device_id,
            "name": device_name,
            "ip": ip_address
        }
        
        response = requests.post(
            f"{server_url}/api/devices/register",
            json=payload,
            timeout=5
        )
        
        if response.status_code == 200:
            print(f"✓ Device registered successfully")
            print(f"  Device ID: {device_id}")
            print(f"  Device Name: {device_name}")
            print(f"  IP Address: {ip_address}")
            return True
        else:
            print(f"✗ Failed to register device: {response.status_code}")
            print(f"  Response: {response.text}")
            return False
    except Exception as e:
        print(f"✗ Error registering device: {e}")
        return False

def keep_alive(server_url, device_id, ip_address):
    """Keep device registration alive by sending periodic updates"""
    print(f"\nKeeping device alive (press Ctrl+C to stop)...")
    try:
        while True:
            try:
                payload = {
                    "device_id": device_id,
                    "ip": ip_address
                }
                response = requests.post(
                    f"{server_url}/api/devices/register",
                    json=payload,
                    timeout=5
                )
                if response.status_code == 200:
                    print(f"[{datetime.now().strftime('%H:%M:%S')}] Device heartbeat sent")
                else:
                    print(f"[{datetime.now().strftime('%H:%M:%S')}] Heartbeat failed: {response.status_code}")
            except Exception as e:
                print(f"[{datetime.now().strftime('%H:%M:%S')}] Heartbeat error: {e}")
            
            time.sleep(30)  # Send heartbeat every 30 seconds
    except KeyboardInterrupt:
        print("\nDevice registration stopped")

if __name__ == "__main__":
    server_url = "http://localhost:8080"
    device_id = get_device_id()
    device_name = get_device_name()
    ip_address = get_local_ip()
    
    print(f"Registering device with SentinelAI server...")
    print(f"Server: {server_url}")
    
    if register_device(server_url, device_id, device_name, ip_address):
        keep_alive(server_url, device_id, ip_address)
