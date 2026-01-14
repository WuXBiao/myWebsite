# 卦云 | 运势预测

一个基于八字理论的运势预测应用，帮助用户通过出生年月日时辰推测自己的运势。

## 🎯 项目概述

**卦云**是一个全栈应用，包含前端和后端两部分：

- **前端**（`guayun-vue`）：Vue 3 + Vite + Tailwind CSS 构建的现代化 Web 应用
- **后端**（`guyun-go`）：Go + Go-Zero 框架构建的高性能 API 服务

## 📁 项目结构

```
guayun/
├── guayun-vue/          # 前端应用
│   ├── src/
│   │   ├── views/       # 页面组件
│   │   ├── router/      # 路由配置
│   │   ├── App.vue      # 根组件
│   │   └── main.js      # 入口
│   ├── package.json
│   ├── vite.config.js
│   └── README.md
├── guyun-go/            # 后端服务
│   ├── main.go          # 主程序
│   ├── config.yaml      # 配置文件
│   ├── go.mod
│   └── README.md
└── README.md            # 本文件
```

## 🚀 快速开始

### 后端启动

```bash
cd guyun-go
go run main.go
```

后端服务将在 `http://localhost:8888` 启动。

### 前端启动

```bash
cd guayun-vue
npm install
npm run dev
```

前端应用将在 `http://localhost:5173` 启动。

## 💡 核心功能

### 1. 八字计算
- 根据出生年月日时辰计算八字
- 天干地支推导
- 五行属性分析

### 2. 运势预测
- **短期运势**：近三个月的运势走向
- **长期运势**：全年的运势预测
- 包含事业、感情、健康、财运等多个维度

### 3. 五行分析
- 五行平衡评估
- 五行旺衰判断

### 4. 幸运信息
- 幸运颜色推荐
- 幸运数字提示
- 幸运方向指引

## 🛠 技术栈

### 前端
- **框架**：Vue 3
- **构建**：Vite
- **样式**：Tailwind CSS
- **路由**：Vue Router
- **状态管理**：Pinia
- **HTTP**：Axios

### 后端
- **语言**：Go 1.21+
- **框架**：Go-Zero
- **API**：RESTful

## 📡 API 接口

### 健康检查
```
GET /api/health
```

### 运势预测
```
POST /api/fortune/predict

Request:
{
  "year": 1990,
  "month": 5,
  "day": 15,
  "hour": 14,
  "minute": 30,
  "gender": "male"
}

Response:
{
  "code": 0,
  "message": "success",
  "data": {
    "bazi": {...},
    "fortune": {...}
  }
}
```

### 获取历史记录
```
GET /api/fortune/history/:userId
```

### 获取运势详情
```
GET /api/fortune/detail/:recordId
```

## 🎨 UI 特性

- 现代化的紫色渐变设计
- 毛玻璃效果（Glassmorphism）
- 响应式布局，支持移动端
- 流畅的动画和过渡效果
- 深色主题，护眼设计

## 📝 使用示例

1. 打开应用首页
2. 输入出生年月日时辰和性别
3. 点击"开始预测"按钮
4. 查看八字信息和运势预测结果
5. 浏览幸运信息和五行分析

## 🔄 后续开发计划

### 前端
- [ ] 用户认证与登录
- [ ] 历史记录保存与查询
- [ ] 分享功能
- [ ] 移动端 APP（React Native/Flutter）
- [ ] PWA 支持

### 后端
- [ ] 数据库集成（MySQL/PostgreSQL）
- [ ] 用户管理系统
- [ ] 更精确的八字算法
- [ ] 缓存机制（Redis）
- [ ] 日志系统
- [ ] 性能优化

## ⚖️ 免责声明

本应用仅供娱乐参考，不构成任何投资、人生决策的依据。运势预测基于传统文化理论，具有一定的主观性。请理性对待预测结果。

## 📄 许可证

MIT

## 👨‍💻 开发者

WuXBiao

---

**祝你好运！** 🍀
