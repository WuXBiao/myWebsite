# 开发指南

本文档提供项目的开发环境设置和开发流程指南。

## 开发环境要求

- Node.js 18+
- npm 或 yarn
- Docker (可选，用于本地测试部署)
- Git

## 前端开发 (my-blog)

### 安装依赖

```bash
cd my-blog
npm install
```

### 开发服务器

```bash
npm run dev
```

访问 http://localhost:5173

### 构建

```bash
npm run build
```

### 本地 Docker 测试

```bash
npm run docker:build
npm run docker:up
```

## 后端开发 (backend-api) - 计划中

### 技术栈选择

考虑使用以下技术栈：

- **框架**: Express.js 或 NestJS
- **数据库**: MongoDB 或 PostgreSQL
- **认证**: JWT
- **文档**: Swagger/OpenAPI
- **测试**: Jest

### 项目结构建议

```
backend-api/
├── src/
│   ├── controllers/
│   ├── models/
│   ├── routes/
│   ├── middleware/
│   ├── services/
│   └── utils/
├── tests/
├── docs/
├── Dockerfile
├── docker-compose.yml
└── package.json
```

## 开发流程

### 分支策略

- `main`: 生产分支
- `develop`: 开发分支
- `feature/*`: 功能分支
- `hotfix/*`: 热修复分支

### 提交规范

使用 Conventional Commits 规范：

```
feat: 添加新功能
fix: 修复 bug
docs: 文档更新
style: 代码格式化
refactor: 代码重构
test: 测试相关
chore: 构建过程或辅助工具的变动
```

### 开发工作流

1. 从 `develop` 分支创建功能分支
2. 开发功能并提交代码
3. 创建 Pull Request 到 `develop`
4. 代码审查通过后合并
5. 定期将 `develop` 合并到 `main` 进行发布

## 代码质量

### ESLint 配置

前端项目已配置 ESLint，运行：

```bash
npm run lint
```

### 代码格式化

使用 Prettier 进行代码格式化：

```bash
npm run format
```

### 测试

建议添加以下测试：

- 单元测试
- 集成测试
- E2E 测试

## 本地开发环境

### 使用 Docker Compose 本地开发

创建 `docker-compose.dev.yml`：

```yaml
version: '3.8'

services:
  frontend:
    build:
      context: ./my-blog
      target: development
    ports:
      - "5173:5173"
    volumes:
      - ./my-blog:/app
      - /app/node_modules

  backend:
    build:
      context: ./backend-api
      target: development
    ports:
      - "3000:3000"
    volumes:
      - ./backend-api:/app
      - /app/node_modules
    environment:
      - NODE_ENV=development

  database:
    image: mongo:latest
    ports:
      - "27017:27017"
    volumes:
      - dev-db-data:/data/db

volumes:
  dev-db-data:
```

## 调试

### 前端调试

使用 Vue DevTools 浏览器扩展进行调试。

### 后端调试

使用 VS Code 的调试功能或 Node.js Inspector。

## 性能优化

### 前端优化

- 代码分割
- 懒加载
- 图片优化
- 缓存策略

### 后端优化

- 数据库索引
- 查询优化
- 缓存机制
- API 限流

## 安全考虑

- 输入验证
- SQL/NoSQL 注入防护
- XSS 防护
- CSRF 防护
- 安全头设置
- 敏感信息保护
