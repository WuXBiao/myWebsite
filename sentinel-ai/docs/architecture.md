# 系统架构设计

## 整体架构
(此处可以放置架构图)

## 模块说明

### 1. Server Go (后端)
负责业务逻辑、API 接口、设备管理、报警处理等。
主要技术：Gin, Gorm, JWT, WebSocket。

### 2. Server Vue (前端)
Web 管理控制台，用于查看监控、管理设备。
主要技术：Vue3, Vite, Element Plus。

### 3. Server App (移动端)
运行在旧手机或监控设备上，负责采集视频流并推送到服务器。
主要技术：Flutter / UniApp / RN。

### 4. AI Service (AI 服务)
负责对视频流或截图进行目标检测（人、车、动物等）。
主要技术：Python, OpenCV, YOLOv8。
