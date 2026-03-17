from flask import Flask, jsonify, request
import cv2
import numpy as np
from ultralytics import YOLO

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
            english_name = yolo_model.names[int(box.cls)]
            chinese_name = get_chinese_name(english_name)
            detections.append({
                "class": chinese_name,
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
                english_name = yolo_model.names[int(box.cls)]
                chinese_name = get_chinese_name(english_name)
                detections.append({
                    "class": chinese_name,
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
