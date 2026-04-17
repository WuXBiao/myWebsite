from flask import Flask, jsonify, request
import cv2
import numpy as np
from ultralytics import YOLO
import requests
from urllib.parse import quote
import json

app = Flask(__name__)

# Load YOLOv8 model for object detection
# Using large model for highest accuracy (yolov8l.pt)
yolo_model = YOLO('yolov8l.pt')

# Chinese translation dictionary for COCO classes
CHINESE_NAMES = {
    'person': '人',
    'bicycle': '自行车',
    'car': '汽车',
    'motorcycle': '摩托车',
    'airplane': '飞机',
    'bus': '公交车',
    'train': '火车',
    'truck': '卡车',
    'boat': '船',
    'traffic light': '红绿灯',
    'fire hydrant': '消防栓',
    'stop sign': '停止标志',
    'parking meter': '停车计时器',
    'bench': '长椅',
    'cat': '猫',
    'dog': '狗',
    'horse': '马',
    'sheep': '羊',
    'cow': '牛',
    'elephant': '大象',
    'bear': '熊',
    'zebra': '斑马',
    'giraffe': '长颈鹿',
    'backpack': '背包',
    'umbrella': '伞',
    'handbag': '手提包',
    'tie': '领带',
    'suitcase': '行李箱',
    'frisbee': '飞盘',
    'skis': '滑雪板',
    'snowboard': '滑雪板',
    'sports ball': '运动球',
    'kite': '风筝',
    'baseball bat': '棒球棒',
    'baseball glove': '棒球手套',
    'skateboard': '滑板',
    'surfboard': '冲浪板',
    'tennis racket': '网球拍',
    'bottle': '瓶子',
    'wine glass': '葡萄酒杯',
    'cup': '杯子',
    'fork': '叉子',
    'knife': '刀',
    'spoon': '勺子',
    'bowl': '碗',
    'banana': '香蕉',
    'apple': '苹果',
    'sandwich': '三明治',
    'orange': '橙子',
    'broccoli': '西兰花',
    'carrot': '胡萝卜',
    'hot dog': '热狗',
    'pizza': '披萨',
    'donut': '甜甜圈',
    'cake': '蛋糕',
    'chair': '椅子',
    'couch': '沙发',
    'potted plant': '盆栽',
    'bed': '床',
    'dining table': '餐桌',
    'toilet': '马桶',
    'tv': '电视',
    'laptop': '笔记本电脑',
    'mouse': '鼠标',
    'remote': '遥控器',
    'keyboard': '键盘',
    'microwave': '微波炉',
    'oven': '烤箱',
    'toaster': '烤面包机',
    'sink': '水槽',
    'refrigerator': '冰箱',
    'book': '书',
    'clock': '时钟',
    'vase': '花瓶',
    'scissors': '剪刀',
    'teddy bear': '泰迪熊',
    'hair drier': '吹风机',
    'toothbrush': '牙刷',
    'pen': '笔',
    'pencil': '铅笔',
    'marker': '记号笔'
}

def get_chinese_name(english_name):
    """Convert English class name to Chinese"""
    english_lower = english_name.lower()
    return CHINESE_NAMES.get(english_lower, english_name)

def detect_gesture_type(landmarks):
    """Detect gesture type based on hand landmarks"""
    if not landmarks or len(landmarks) < 21:
        return "未知"
    
    # Key landmark indices
    THUMB_TIP = 4
    INDEX_TIP = 8
    MIDDLE_TIP = 12
    RING_TIP = 16
    PINKY_TIP = 20
    WRIST = 0
    
    # Get landmark positions
    thumb = landmarks[THUMB_TIP]
    index = landmarks[INDEX_TIP]
    middle = landmarks[MIDDLE_TIP]
    ring = landmarks[RING_TIP]
    pinky = landmarks[PINKY_TIP]
    wrist = landmarks[WRIST]
    
    # Helper function to check if finger is extended
    def is_extended(tip, base_idx):
        base = landmarks[base_idx]
        return tip['y'] < base['y'] - 0.05
    
    # Check finger extensions
    thumb_extended = is_extended(thumb, 2)
    index_extended = is_extended(index, 6)
    middle_extended = is_extended(middle, 10)
    ring_extended = is_extended(ring, 14)
    pinky_extended = is_extended(pinky, 18)
    
    # Detect specific gestures
    extended_count = sum([index_extended, middle_extended, ring_extended, pinky_extended])
    
    # Peace sign (V gesture)
    if index_extended and middle_extended and not ring_extended and not pinky_extended:
        return "胜利手势"
    
    # Thumbs up
    if thumb_extended and not index_extended and not middle_extended and not ring_extended and not pinky_extended:
        if thumb['y'] < wrist['y']:
            return "竖起大拇指"
    
    # OK gesture
    if not index_extended and not middle_extended and ring_extended and pinky_extended:
        return "OK手势"
    
    # Open palm (all fingers extended)
    if index_extended and middle_extended and ring_extended and pinky_extended:
        return "张开手掌"
    
    # Closed fist (no fingers extended)
    if not index_extended and not middle_extended and not ring_extended and not pinky_extended:
        return "握拳"
    
    # Pointing gesture
    if index_extended and not middle_extended and not ring_extended and not pinky_extended:
        return "指向"
    
    return "其他手势"

# MediaPipe initialization
MEDIAPIPE_AVAILABLE = False
hand_landmarker = None
try:
    from mediapipe.tasks import python
    from mediapipe.tasks.python import vision
    import mediapipe as mp
    
    # Create hand landmarker with model file
    model_path = 'hand_landmarker.task'
    base_options = python.BaseOptions(model_asset_path=model_path)
    options = vision.HandLandmarkerOptions(
        base_options=base_options,
        num_hands=2,
        min_hand_detection_confidence=0.5,
        min_hand_presence_confidence=0.5,
        min_tracking_confidence=0.5
    )
    hand_landmarker = vision.HandLandmarker.create_from_options(options)
    MEDIAPIPE_AVAILABLE = True
    print("MediaPipe hand landmarker loaded successfully")
except Exception as e:
    print(f"Warning: MediaPipe not available: {e}")
    print("Gesture recognition will be disabled")
    MEDIAPIPE_AVAILABLE = False
    hand_landmarker = None

def decode_image_from_request():
    """Decode image from request"""
    if 'image' not in request.files:
        return None, jsonify({"error": "No image provided"}), 400
    
    file = request.files['image']
    img_bytes = file.read()
    nparr = np.frombuffer(img_bytes, np.uint8)
    img = cv2.imdecode(nparr, cv2.IMREAD_COLOR)
    
    if img is None:
        return None, jsonify({"error": "Invalid image"}), 400
    
    return img, None, None

def detect_objects(img):
    """Detect objects using YOLOv8"""
    results = yolo_model(img, conf=0.45, iou=0.4)
    detections = []
    for result in results:
        for box in result.boxes:
            english_name = yolo_model.names[int(box.cls)]
            chinese_name = get_chinese_name(english_name)
            detections.append({
                "class": chinese_name,
                "confidence": float(box.conf),
                "bbox": box.xyxy.tolist()[0]
            })
    return detections

def recognize_gestures(img):
    """Recognize hand gestures using MediaPipe"""
    if not MEDIAPIPE_AVAILABLE or hand_landmarker is None:
        return []
    
    try:
        img_rgb = cv2.cvtColor(img, cv2.COLOR_BGR2RGB)
        from mediapipe import Image as MPImage
        mp_image = MPImage(image_format=mp.ImageFormat.SRGB, data=img_rgb)
        detection_result = hand_landmarker.detect(mp_image)
        
        gestures = []
        if detection_result.hand_landmarks:
            for hand_landmarks, handedness in zip(detection_result.hand_landmarks, detection_result.handedness):
                landmarks = [{"x": lm.x, "y": lm.y, "z": lm.z} for lm in hand_landmarks]
                gesture_type = detect_gesture_type(landmarks)
                hand_label = handedness[0].category_name
                hand_chinese = "右手" if hand_label == "Right" else "左手"
                
                gestures.append({
                    "hand": hand_chinese,
                    "gesture": gesture_type,
                    "confidence": float(handedness[0].score),
                    "landmarks": landmarks
                })
        return gestures
    except Exception as e:
        print(f"Gesture recognition error: {e}")
        return []

@app.route('/health', methods=['GET'])
def health():
    return jsonify({"status": "ok", "service": "ai-service"})

@app.route('/detect', methods=['POST'])
def detect():
    """Object detection using YOLOv8"""
    img, error_response, error_code = decode_image_from_request()
    if img is None:
        return error_response, error_code
    
    detections = detect_objects(img)
    return jsonify({
        "message": "Detection successful",
        "objects": detections
    })

@app.route('/recognize-gesture', methods=['POST'])
def recognize_gesture():
    """Hand gesture recognition using MediaPipe"""
    img, error_response, error_code = decode_image_from_request()
    if img is None:
        return error_response, error_code
    
    gestures = recognize_gestures(img)
    return jsonify({
        "message": "Gesture recognition successful",
        "gestures": gestures
    })

@app.route('/analyze', methods=['POST'])
def analyze():
    """Combined analysis: object detection + gesture recognition"""
    img, error_response, error_code = decode_image_from_request()
    if img is None:
        return error_response, error_code
    
    try:
        detections = detect_objects(img)
        gestures = recognize_gestures(img)
        
        return jsonify({
            "message": "Analysis successful",
            "objects": detections,
            "gestures": gestures
        })
    except Exception as e:
        return jsonify({"error": f"Analysis error: {str(e)}"}), 500

def search_baidu_baike(query):
    """Search Baidu Baike for information about a query"""
    try:
        # Try Baidu Baike API endpoint
        url = "https://baike.baidu.com/api/openapi/BaikeLemmaPageV2"
        params = {
            "lemma": query,
            "format": "json"
        }
        
        headers = {
            "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36"
        }
        
        response = requests.get(url, params=params, headers=headers, timeout=5)
        if response.status_code == 200:
            data = response.json()
            if data.get("data"):
                baike_data = data["data"]
                return {
                    "title": baike_data.get("lemmaTitle", query),
                    "description": baike_data.get("lemmaAbstract", ""),
                    "url": f"https://baike.baidu.com/item/{quote(query)}",
                    "source": "baike"
                }
    except Exception as e:
        print(f"Baidu Baike API error: {e}")
    
    # Fallback: Return Baidu Baike URL for manual search
    return {
        "title": query,
        "description": "Click the link below to view information on Baidu Baike",
        "url": f"https://baike.baidu.com/item/{quote(query)}",
        "source": "baike_url"
    }

def search_image_source_and_objects(image_data):
    """Search for image source and detect objects in the image"""
    try:
        # Decode image
        nparr = np.frombuffer(image_data, np.uint8)
        img = cv2.imdecode(nparr, cv2.IMREAD_COLOR)
        
        if img is None:
            return None
        
        # Detect objects in the image
        results = yolo_model(img, conf=0.45, iou=0.4)
        detected_objects = []
        
        for result in results:
            for box in result.boxes:
                english_name = yolo_model.names[int(box.cls)]
                chinese_name = get_chinese_name(english_name)
                confidence = float(box.conf)
                
                # Get Baidu Baike info for each detected object
                baike_info = search_baidu_baike(chinese_name)
                
                detected_objects.append({
                    "name": chinese_name,
                    "english_name": english_name,
                    "confidence": confidence,
                    "baike": baike_info
                })
        
        # Try to find image source using TinEye API or similar
        # For now, we'll use a placeholder that indicates the image was analyzed
        image_source_info = {
            "status": "analyzed",
            "message": "Image analyzed successfully",
            "note": "Use reverse image search tools like TinEye or Google Images for source"
        }
        
        return {
            "objects": detected_objects,
            "source": image_source_info
        }
    except Exception as e:
        print(f"Image search error: {e}")
    
    return None

@app.route('/search-object', methods=['POST'])
def search_object():
    """Search for object information using Baidu Baike"""
    if 'object_name' not in request.json:
        return jsonify({"error": "No object name provided"}), 400
    
    object_name = request.json['object_name']
    
    baike_info = search_baidu_baike(object_name)
    
    if baike_info:
        return jsonify({
            "message": "Search successful",
            "baike": baike_info
        })
    else:
        return jsonify({
            "message": "No information found",
            "baike": None
        })

@app.route('/search-image-source', methods=['POST'])
def search_image_source_endpoint():
    """Search for image source and detect objects with Baike info"""
    if 'image' not in request.files:
        return jsonify({"error": "No image provided"}), 400
    
    file = request.files['image']
    img_bytes = file.read()
    
    result = search_image_source_and_objects(img_bytes)
    
    if result:
        return jsonify({
            "message": "Image analysis successful",
            "data": result
        })
    else:
        return jsonify({
            "error": "Failed to analyze image"
        }), 500

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5001)
