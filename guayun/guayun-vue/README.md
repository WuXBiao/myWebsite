# 卦云|运势预测 - 前端应用

基于 Vue 3 + Vite 的运势预测前端应用。

## 功能特性

- 八字信息输入与展示
- 运势预测与分析
- 五行平衡评估
- 幸运信息提示
- 历史记录管理

## 项目结构

```
guayun-vue/
├── src/
│   ├── views/           # 页面组件
│   │   ├── Home.vue     # 首页（预测页面）
│   │   ├── History.vue  # 历史记录
│   │   └── About.vue    # 关于页面
│   ├── router/          # 路由配置
│   ├── App.vue          # 根组件
│   ├── main.js          # 入口文件
│   └── style.css        # 全局样式
├── index.html           # HTML 模板
├── vite.config.js       # Vite 配置
├── tailwind.config.js   # Tailwind 配置
├── postcss.config.js    # PostCSS 配置
└── package.json         # 项目依赖
```

## 安装依赖

```bash
npm install
```

## 开发

```bash
npm run dev
```

应用将在 `http://localhost:5173` 启动。

## 构建

```bash
npm run build
```

## 预览

```bash
npm run preview
```

## 技术栈

- Vue 3 - 前端框架
- Vite - 构建工具
- Vue Router - 路由管理
- Pinia - 状态管理
- Axios - HTTP 客户端
- Tailwind CSS - 样式框架

## 后端 API

确保后端服务运行在 `http://localhost:8888`

### 运势预测接口

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
```

## 后续开发计划

- [ ] 用户认证与登录
- [ ] 历史记录保存
- [ ] 数据库集成
- [ ] 更精确的八字算法
- [ ] 移动端适配优化
- [ ] 分享功能
