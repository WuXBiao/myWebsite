# 我的个人博客

这是一个基于 Vue 3、Vite 和 Tailwind CSS 构建的个人博客网站，参考了 [blog.usword.cn](https://blog.usword.cn) 的设计风格。

## 功能特点

- 响应式设计，适配各种屏幕尺寸
- 深色模式支持
- 文章分类浏览
- Markdown 文章渲染
- 代码高亮显示
- 文章搜索功能

## 技术栈

- **前端框架**：Vue 3
- **构建工具**：Vite
- **路由管理**：Vue Router
- **状态管理**：Pinia
- **CSS 框架**：Tailwind CSS
- **HTTP 客户端**：Axios
- **Markdown 解析**：Marked
- **代码高亮**：highlight.js

## 快速开始

### 安装依赖

```bash
npm install
```

### 开发模式

```bash
npm run dev
```

### 构建生产版本

```bash
npm run build
```

### 预览生产版本

```bash
npm run preview
```

## 项目结构

```
/src
  /api        # API 请求
  /assets     # 静态资源
  /components # 组件
    /common   # 通用组件
    /layout   # 布局组件
  /router     # 路由配置
  /store      # 状态管理
  /utils      # 工具函数
  /views      # 页面视图
  App.vue     # 根组件
  main.js     # 入口文件
```

## 自定义配置

请参考 [Vite 配置指南](https://vitejs.dev/config/)。

## 许可证

MIT
