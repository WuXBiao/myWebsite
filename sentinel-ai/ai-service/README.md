# AI Recognition Service

Python AI服务，提供手势识别和物体检测功能。

## 功能

- **物体检测**：使用YOLOv8进行实时物体检测
- **手势识别**：使用MediaPipe进行手势识别
- **综合分析**：同时进行物体检测和手势识别

## 安装

### 1. 安装Python依赖

```bash
pip install -r requirements.txt
```

### 2. 运行服务

```bash
python app.py
```

服务将在 `http://localhost:5001` 启动

## API接口

### 健康检查

```
GET /health
```

返回服务状态。

### 物体检测

```
POST /detect
Content-Type: multipart/form-data

image: <image_file>
```

返回检测到的物体列表。

### 手势识别

```
POST /recognize-gesture
Content-Type: multipart/form-data

image: <image_file>
```

返回识别到的手势和关键点。

### 综合分析

```
POST /analyze
Content-Type: multipart/form-data

image: <image_file>
```

同时返回物体检测和手势识别结果。

## 响应格式

### 物体检测响应

```json
{
  "message": "Detection successful",
  "objects": [
    {
      "class": "person",
      "confidence": 0.95,
      "bbox": [100, 50, 200, 300]
    }
  ]
}
```

### 手势识别响应

```json
{
  "message": "Gesture recognition successful",
  "gestures": [
    {
      "hand": "Right",
      "confidence": 0.98,
      "landmarks": [
        {"x": 0.5, "y": 0.5, "z": 0.0},
        ...
      ]
    }
  ]
}
```

### 综合分析响应

```json
{
  "message": "Analysis successful",
  "objects": [...],
  "gestures": [...]
}
```

## 配置

### 环境变量

- `FLASK_ENV`: Flask环境（development/production）
- `FLASK_DEBUG`: 调试模式（0/1）

## 性能优化

- 使用YOLOv8n（纳米版本）以获得最佳性能
- MediaPipe使用静态图像模式，适合实时处理
- 建议每500ms发送一帧进行分析

## 依赖

- Flask: Web框架
- OpenCV: 图像处理
- YOLOv8: 物体检测
- MediaPipe: 手势识别
- NumPy: 数值计算
