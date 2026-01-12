# 我的个人网站项目

这是一个全栈个人网站项目，包含前端博客和后端 API 服务。

## 项目结构

```
myWebSite/
├── .github/
│   └── workflows/          # GitHub Actions 工作流
│       ├── frontend-deploy.yml    # 前端部署
│       ├── backend-deploy.yml     # 后端部署
│       └── full-stack-deploy.yml  # 全栈部署
├── my-blog/               # 前端博客项目 (Vue 3)
│   ├── src/
│   ├── public/
│   ├── Dockerfile
│   ├── docker-compose.yml
│   └── ...
├── backend-api/           # 后端 API 服务 (计划中)
│   ├── src/
│   ├── Dockerfile
│   ├── docker-compose.yml
│   └── ...
├── deployment/            # 部署相关文件
│   ├── docker-compose.prod.yml   # 生产环境 Docker Compose
│   ├── nginx.conf                # Nginx 配置
│   └── deploy-scripts/           # 部署脚本
└── docs/                  # 项目文档
    ├── deployment.md
    └── development.md
```

## 技术栈

### 前端 (my-blog)
- **框架**: Vue 3 + Vite
- **状态管理**: Pinia
- **路由**: Vue Router
- **样式**: Tailwind CSS
- **部署**: Docker + Nginx

### 后端 (backend-api) - 计划中
- **框架**: Node.js + Express / NestJS
- **数据库**: MongoDB / PostgreSQL
- **认证**: JWT
- **部署**: Docker

## 快速开始

### 前端开发

```bash
cd my-blog
npm install
npm run dev
```

### 部署

项目使用 GitHub Actions 自动化部署：

1. **前端部署**: 推送到 `my-blog/` 目录的更改会触发前端部署
2. **后端部署**: 推送到 `backend-api/` 目录的更改会触发后端部署
3. **全栈部署**: 推送到 main 分支会触发所有服务的部署

## CI/CD 流程

### 前端部署流程
1. 检测到 `my-blog/` 目录的更改
2. 构建 Vue 应用
3. 创建 Docker 镜像
4. 推送到 Docker Hub
5. 服务器拉取最新镜像并部署

### 后端部署流程
1. 检测到 `backend-api/` 目录的更改
2. 构建 Node.js 应用
3. 创建 Docker 镜像
4. 推送到 Docker Hub
5. 服务器拉取最新镜像并部署

## 部署到服务器

详细的部署说明请参考：
- [前端部署文档](./my-blog/DEPLOY.md)
- [后端部署文档](./backend-api/DEPLOY.md) (计划中)

## 环境变量

在 GitHub 仓库的 Secrets 中设置以下变量：

- `DOCKER_USERNAME`: Docker Hub 用户名
- `DOCKER_PASSWORD`: Docker Hub 密码或访问令牌

## 许可证

MIT
