# AI识别功能设置指南

## 启动顺序

### 1. Python AI服务（必须先启动）

```bash
cd ai-service
pip install -r requirements.txt
python app.py
```

服务运行在 `http://localhost:5001`

### 2. Go后端

```bash
cd server-go
go run main.go
```

后端运行在 `http://localhost:8080`

### 3. Vue前端

```bash
cd server-vue
npm run dev
```

前端运行在 `http://localhost:5173`

## 使用步骤

1. 登录系统
2. 打开CameraView页面（Dashboard或Devices中点击View按钮）
3. 点击"Start Camera"启动摄像头
4. 点击"AI: OFF"按钮启用AI识别
5. 查看实时识别结果

## API端点

- `POST /api/ai/analyze` - 综合分析（物体+手势）
- `POST /api/ai/detect` - 物体检测
- `POST /api/ai/gesture` - 手势识别

## 识别结果显示

- 视频画面：绿色框=物体，红色点=手势
- 结果面板：显示检测到的物体和手势置信度
