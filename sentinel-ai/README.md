# SentinelAI - AI智能监控系统

## 一、项目简介
SentinelAI 是一个基于 AI 视觉识别的智能监控系统。
系统通过移动端摄像头采集视频流，后端进行 AI 识别，并在 Web 控制台展示实时监控信息与报警。

## 二、快速开始

### 1. 环境准备
- Go 1.22+
- Node.js 18+
- Python 3.9+

### 2. 启动 AI 服务
```bash
cd ai-service
pip install -r requirements.txt
python3 app.py
# 服务运行在 http://localhost:5001 (注意：端口已改为 5001 以避免冲突)
``````

### 3. 启动后端服务
```bash
cd server-go
go mod tidy
go run main.go
# 服务运行在 http://localhost:8080
```

### 4. 启动前端控制台
```bash
cd server-vue
npm install
npm run dev
# 访问 http://localhost:5173
```

### 5. 模拟设备推流 (可选)
如果你没有移动端环境，可以使用提供的 Python 脚本模拟设备推流：
```bash
pip install websocket-client opencv-python
python3 scripts/simulate_device.py device_01
```
这将开启你的电脑摄像头并将视频流推送到服务器。

## 三、系统架构
... (保持原有内容)

## 四、功能验证流程
1. 打开 Web 控制台 (http://localhost:5173)。
2. 注册并登录账号。
3. 添加一个设备，设备ID 输入 `device_01` (与模拟脚本中的ID一致)。
4. 运行模拟推流脚本。
5. 在控制台点击 "View" 按钮，即可看到实时视频流。
6. 如果画面中出现人或物体，AI 服务将自动检测并记录事件（查看后端日志）。
