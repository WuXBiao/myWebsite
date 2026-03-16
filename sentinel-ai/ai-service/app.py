from flask import Flask, jsonify, request
import cv2
import numpy as np
from ultralytics import YOLO

app = Flask(__name__)

# Load YOLOv8 model for object detection
# Using large model for highest accuracy (yolov8l.pt)
yolo_model = YOLO('yolov8l.pt')

# MediaPipe initialization (optional)
MEDIAPIPE_AVAILABLE = False
hands = None
try:
    from mediapipe.python.solutions import hands as mp_hands
    hands = mp_hands.Hands(
        static_image_mode=True,
        max_num_hands=2,
        min_detection_confidence=0.5,
        min_tracking_confidence=0.5
    )
    MEDIAPIPE_AVAILABLE = True
    print("MediaPipe loaded successfully")
except Exception as e:
    print(f"Warning: MediaPipe not available: {e}")
    print("Gesture recognition will be disabled")

@app.route('/health', methods=['GET'])
def health():
    return jsonify({"status": "ok", "service": "ai-service"})

@app.route('/detect', methods=['POST'])
def detect():
    """Object detection using YOLOv8"""
    if 'image' not in request.files:
        return jsonify({"error": "No image provided"}), 400
    
    file = request.files['image']
    img_bytes = file.read()
    nparr = np.frombuffer(img_bytes, np.uint8)
    img = cv2.imdecode(nparr, cv2.IMREAD_COLOR)
    
    if img is None:
        return jsonify({"error": "Invalid image"}), 400

    # Run inference with optimized parameters
    # conf: confidence threshold (0.45 for better accuracy)
    # iou: IoU threshold for NMS (0.4 for stricter filtering)
    results = yolo_model(img, conf=0.45, iou=0.4)
    
    detections = []
    for result in results:
        for box in result.boxes:
            detections.append({
                "class": yolo_model.names[int(box.cls)],
                "confidence": float(box.conf),
                "bbox": box.xyxy.tolist()[0]
            })
            
    return jsonify({
        "message": "Detection successful",
        "objects": detections
    })

@app.route('/recognize-gesture', methods=['POST'])
def recognize_gesture():
    """Hand gesture recognition using MediaPipe"""
    if 'image' not in request.files:
        return jsonify({"error": "No image provided"}), 400
    
    if not MEDIAPIPE_AVAILABLE or hands is None:
        return jsonify({"error": "MediaPipe not available"}), 503
    
    file = request.files['image']
    img_bytes = file.read()
    nparr = np.frombuffer(img_bytes, np.uint8)
    img = cv2.imdecode(nparr, cv2.IMREAD_COLOR)
    
    if img is None:
        return jsonify({"error": "Invalid image"}), 400

    try:
        # Convert BGR to RGB
        img_rgb = cv2.cvtColor(img, cv2.COLOR_BGR2RGB)
        results = hands.process(img_rgb)
        
        gestures = []
        if results.multi_hand_landmarks:
            for hand_landmarks, handedness in zip(results.multi_hand_landmarks, results.multi_handedness):
                # Extract hand landmarks
                landmarks = []
                for landmark in hand_landmarks.landmark:
                    landmarks.append({
                        "x": landmark.x,
                        "y": landmark.y,
                        "z": landmark.z
                    })
                
                gestures.append({
                    "hand": handedness.classification[0].label,
                    "confidence": float(handedness.classification[0].score),
                    "landmarks": landmarks
                })
        
        return jsonify({
            "message": "Gesture recognition successful",
            "gestures": gestures
        })
    except Exception as e:
        return jsonify({"error": f"Gesture recognition error: {str(e)}"}), 500

@app.route('/analyze', methods=['POST'])
def analyze():
    """Combined analysis: object detection + gesture recognition"""
    if 'image' not in request.files:
        return jsonify({"error": "No image provided"}), 400
    
    file = request.files['image']
    img_bytes = file.read()
    nparr = np.frombuffer(img_bytes, np.uint8)
    img = cv2.imdecode(nparr, cv2.IMREAD_COLOR)
    
    if img is None:
        return jsonify({"error": "Invalid image"}), 400

    try:
        # Object detection with optimized parameters
        yolo_results = yolo_model(img, conf=0.45, iou=0.4)
        detections = []
        for result in yolo_results:
            for box in result.boxes:
                detections.append({
                    "class": yolo_model.names[int(box.cls)],
                    "confidence": float(box.conf),
                    "bbox": box.xyxy.tolist()[0]
                })
        
        # Gesture recognition
        gestures = []
        if MEDIAPIPE_AVAILABLE and hands is not None:
            try:
                img_rgb = cv2.cvtColor(img, cv2.COLOR_BGR2RGB)
                gesture_results = hands.process(img_rgb)
                
                if gesture_results.multi_hand_landmarks:
                    for hand_landmarks, handedness in zip(gesture_results.multi_hand_landmarks, gesture_results.multi_handedness):
                        landmarks = []
                        for landmark in hand_landmarks.landmark:
                            landmarks.append({
                                "x": landmark.x,
                                "y": landmark.y,
                                "z": landmark.z
                            })
                        
                        gestures.append({
                            "hand": handedness.classification[0].label,
                            "confidence": float(handedness.classification[0].score),
                            "landmarks": landmarks
                        })
            except Exception as e:
                print(f"Gesture recognition error: {e}")
                # Continue with object detection results even if gesture recognition fails
        
        return jsonify({
            "message": "Analysis successful",
            "objects": detections,
            "gestures": gestures
        })
    except Exception as e:
        return jsonify({"error": f"Analysis error: {str(e)}"}), 500

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5001)
