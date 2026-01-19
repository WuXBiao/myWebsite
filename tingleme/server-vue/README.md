# 录音管理平台 - 前端

基于 Vue.js 的录音管理平台前端界面。

## 技术栈

- Vue 3
- Vue Router
- Element Plus UI 组件库
- Axios HTTP 客户端

## 功能特性

- 录音列表展示（支持分页和筛选）
- 录音上传功能
- 录音在线试听
- 录音删除功能

## 快速开始

### 环境要求

- Node.js 12+
- npm 或 yarn

### 安装步骤

1. 安装依赖包：

```bash
npm install
```

2. 启动开发服务器：

```bash
npm run serve
```

应用将在 `http://localhost:8080` 启动。

### 构建生产版本

```bash
npm run build
```

构建后的文件在 `dist/` 目录，可部署到 Web 服务器。

## 项目结构

```
src/
├── api/              # API 服务封装
├── components/       # 公共组件
├── views/            # 页面视图
├── router/           # 路由配置
├── assets/           # 静态资源
└── main.js           # 应用入口
```

## API 配置

API 服务配置在 `src/api/recordingApi.js` 文件中：

```javascript
baseURL: 'http://localhost:8080/api'
```

## 主要页面

- `/` - 首页
- `/recordings` - 录音列表页（支持分页和筛选）
- `/upload` - 录音上传页