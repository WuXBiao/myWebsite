# 部署指南

本文档提供完整的部署指南，包括前端、后端和全栈部署。

## 项目结构

```
myWebSite/
├── .github/workflows/     # CI/CD 工作流
├── my-blog/              # 前端博客项目
├── backend-api/          # 后端 API 项目 (计划中)
├── deployment/           # 部署配置
│   ├── docker-compose.prod.yml
│   ├── nginx.conf
│   ├── deploy-scripts/
│   └── .env.example
└── docs/                 # 项目文档
```

## CI/CD 工作流

### 1. 前端部署 (frontend-deploy.yml)
- 触发条件：`my-blog/` 目录有更改
- 构建 Vue 应用并推送到 Docker Hub

### 2. 后端部署 (backend-deploy.yml)
- 触发条件：`backend-api/` 目录有更改
- 构建 Node.js 应用并推送到 Docker Hub

### 3. 全栈部署 (full-stack-deploy.yml)
- 触发条件：推送到 main 分支
- 构建所有服务并推送到 Docker Hub

## 服务器部署

### 仅前端部署

如果只需要部署前端博客：

```bash
# 1. 上传必要文件到服务器
scp my-blog/docker-compose.yml user@server:~/
scp my-blog/nginx.conf user@server:~/
scp my-blog/deploy.sh user@server:~/

# 2. 在服务器上执行
export DOCKER_USERNAME=yourusername
./deploy.sh
```

### 全栈部署

如果需要部署完整的全栈应用：

```bash
# 1. 上传部署文件到服务器
scp -r deployment/ user@server:~/

# 2. 在服务器上执行
cd deployment
cp .env.example .env  # 编辑 .env 文件
chmod +x deploy-scripts/*.sh
./deploy-scripts/deploy-fullstack.sh
```

## 环境配置

### GitHub Secrets

在 GitHub 仓库设置中添加以下 Secrets：

- `DOCKER_USERNAME`: Docker Hub 用户名
- `DOCKER_PASSWORD`: Docker Hub 密码或访问令牌

### 服务器环境变量

创建 `.env` 文件并配置以下变量：

```bash
DOCKER_USERNAME=yourusername
DATABASE_URL=mongodb://username:password@database:27017/myblog
DB_USERNAME=admin
DB_PASSWORD=your_secure_password
JWT_SECRET=your_jwt_secret_key_here
```

## 监控和维护

### 查看容器状态

```bash
docker ps
docker logs container_name
```

### 更新服务

```bash
# 拉取最新镜像并重启
./deploy-scripts/deploy-fullstack.sh
```

### 备份数据

```bash
# 备份数据库
docker exec my-blog-db mongodump --out /backup

# 备份配置文件
tar -czf backup-$(date +%Y%m%d).tar.gz deployment/
```
