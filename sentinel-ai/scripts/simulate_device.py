import cv2
import websocket
import time
import sys
import numpy as np
import datetime

def create_mock_frame():
    # Create a black image
    frame = np.zeros((480, 640, 3), np.uint8)
    
    # Add moving text (time)
    text = datetime.datetime.now().strftime("%Y-%m-%d %H:%M:%S.%f")[:-3]
    
    # Moving position
    t = time.time()
    x = int(320 + 100 * np.sin(t))
    y = int(240 + 100 * np.cos(t))
    
    # Draw a circle
    cv2.circle(frame, (x, y), 50, (0, 255, 0), -1)
    
    # Put text
    cv2.putText(frame, text, (50, 50), cv2.FONT_HERSHEY_SIMPLEX, 1, (255, 255, 255), 2)
    cv2.putText(frame, "Mock Device Stream", (50, 100), cv2.FONT_HERSHEY_SIMPLEX, 0.8, (0, 255, 255), 2)
    
    return frame

def on_open(ws):
    print("Connected to server")
    
    # Try opening webcam
    cap = cv2.VideoCapture(0)
    use_mock = False
    
    if not cap.isOpened():
        print("Cannot open camera, switching to MOCK mode")
        use_mock = True
    else:
        # Check if we can actually read a frame
        ret, frame = cap.read()
        if not ret:
            print("Cannot read from camera, switching to MOCK mode")
            use_mock = True
            cap.release()

    try:
        while True:
            if use_mock:
                frame = create_mock_frame()
                time.sleep(0.1) # Limit FPS for mock
            else:
                ret, frame = cap.read()
                if not ret:
                    break
                
            # Resize to reduce bandwidth
            frame = cv2.resize(frame, (640, 480))
            
            # Encode as JPEG
            _, buffer = cv2.imencode('.jpg', frame, [int(cv2.IMWRITE_JPEG_QUALITY), 50])
            
            # Send binary
            ws.send(buffer.tobytes(), opcode=websocket.ABNF.OPCODE_BINARY)
            
            if not use_mock:
                time.sleep(0.05) # Limit FPS for camera
                
    except KeyboardInterrupt:
        pass
    except Exception as e:
        print(f"Error in loop: {e}")
    finally:
        if not use_mock and cap.isOpened():
            cap.release()
        ws.close()

def on_error(ws, error):
    print(f"Error: {error}")

def on_close(ws, close_status_code, close_msg):
    print("Connection closed")

if __name__ == "__main__":
    device_id = "device1"
    if len(sys.argv) > 1:
        device_id = sys.argv[1]
        
    url = f"ws://localhost:8080/ws/stream/{device_id}"
    print(f"Connecting to {url}...")
    
    ws = websocket.WebSocketApp(url,
                                on_open=on_open,
                                on_error=on_error,
                                on_close=on_close)
    ws.run_forever()
