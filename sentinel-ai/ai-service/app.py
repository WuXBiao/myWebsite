from flask import Flask, jsonify, request
import cv2
import numpy as np
from ultralytics import YOLO

app = Flask(__name__)
# Load model. It will download yolov8n.pt on first run
model = YOLO('yolov8n.pt')

@app.route('/health', methods=['GET'])
def health():
    return jsonify({"status": "ok", "service": "ai-service"})

@app.route('/detect', methods=['POST'])
def detect():
    if 'image' not in request.files:
        return jsonify({"error": "No image provided"}), 400
    
    file = request.files['image']
    img_bytes = file.read()
    nparr = np.frombuffer(img_bytes, np.uint8)
    img = cv2.imdecode(nparr, cv2.IMREAD_COLOR)
    
    if img is None:
         return jsonify({"error": "Invalid image"}), 400

    results = model(img)
    
    detections = []
    for result in results:
        for box in result.boxes:
            detections.append({
                "class": model.names[int(box.cls)],
                "confidence": float(box.conf),
                "bbox": box.xyxy.tolist()[0]
            })
            
    return jsonify({
        "message": "Detection successful",
        "objects": detections
    })

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5001)
