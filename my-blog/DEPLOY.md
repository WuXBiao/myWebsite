# 使用 GitHub Actions 和 Docker Hub 部署到阿里云服务器

本文档提供将个人博客项目通过 GitHub Actions 和 Docker Hub 部署到阿里云服务器的详细步骤。这种方式可以节省服务器磁盘空间，因为源代码不需要存储在服务器上。

## 前提条件

1. 已购买阿里云 ECS 服务器
2. 已安装 Docker 和 Docker Compose
3. 已配置服务器安全组，开放 80 端口
4. 拥有 GitHub 账号和 Docker Hub 账号

## 部署流程概述

1. 将代码推送到 GitHub 仓库
2. GitHub Actions 自动构建 Docker 镜像并推送到 Docker Hub
3. 服务器从 Docker Hub 拉取最新镜像并部署

## 详细步骤

### 1. GitHub 和 Docker Hub 设置

#### 1.1 创建 GitHub 仓库

将项目代码推送到 GitHub 仓库。

#### 1.2 设置 GitHub Secrets

在 GitHub 仓库中添加以下 Secrets：

- `DOCKER_USERNAME`: 您的 Docker Hub 用户名
- `DOCKER_PASSWORD`: 您的 Docker Hub 密码或访问令牌

步骤：
1. 进入仓库 → Settings → Secrets and variables → Actions
2. 点击 "New repository secret"
3. 添加上述 secrets

### 2. 服务器准备

#### 2.1 安装 Docker 和 Docker Compose

```bash
# 安装 Docker
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh

# 启动 Docker 服务
sudo systemctl start docker
sudo systemctl enable docker

# 安装 Docker Compose
sudo curl -L "https://github.com/docker/compose/releases/download/v2.20.0/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose
```

#### 2.2 准备部署文件

在服务器上创建一个目录用于部署：

```bash
mkdir -p ~/my-blog
cd ~/my-blog
```

只需要将以下文件上传到服务器：

1. `docker-compose.yml`
2. `nginx.conf`
3. `deploy.sh`
4. `.env` (从 .env.example 创建)

```bash
# 创建 .env 文件
echo "DOCKER_USERNAME=yourusername" > .env
```

### 3. 部署流程

#### 3.1 推送代码到 GitHub

每当您推送代码到 GitHub 的 main 分支时，GitHub Actions 会自动构建 Docker 镜像并推送到 Docker Hub。

#### 3.2 在服务器上部署

```bash
# 进入部署目录
cd ~/my-blog

# 给部署脚本添加执行权限
chmod +x deploy.sh

# 设置 Docker Hub 用户名环境变量
export DOCKER_USERNAME=yourusername

# 执行部署脚本
./deploy.sh
```

### 4. 验证部署

在浏览器中访问服务器 IP 地址或域名，确认博客网站已成功部署。

## 更新网站

当您需要更新网站内容时：

1. 修改代码并推送到 GitHub
2. GitHub Actions 会自动构建新的 Docker 镜像
3. 在服务器上执行部署脚本拉取最新镜像

```bash
cd ~/my-blog
./deploy.sh
```

## 常见问题排查

### 1. GitHub Actions 构建失败

检查 GitHub Actions 日志，确保 Secrets 已正确设置。

### 2. 容器无法启动

检查 Docker 日志：

```bash
docker logs my-personal-blog
```

### 3. 网站无法访问

- 确认安全组已开放 80 端口
- 检查 Nginx 配置是否正确
- 验证容器是否正在运行：`docker ps`

## 使用 HTTPS（可选）

如果需要配置 HTTPS，可以使用 Certbot 和 Let's Encrypt：

1. 修改 docker-compose.yml，添加 443 端口映射
2. 更新 Nginx 配置以支持 HTTPS
3. 使用 Certbot 获取 SSL 证书

## 备份策略

由于我们的源代码存储在 GitHub 上，服务器上只需要备份配置文件和日志：

```bash
# 创建备份目录
mkdir -p ~/backups

# 备份配置文件
tar -czf ~/backups/my-blog-config-$(date +%Y%m%d).tar.gz ~/my-blog/*.yml ~/my-blog/*.conf ~/my-blog/*.sh ~/my-blog/.env

# 备份日志（如果需要）
tar -czf ~/backups/my-blog-logs-$(date +%Y%m%d).tar.gz ~/my-blog/logs
```

## 优势

1. **节省服务器磁盘空间**：源代码和构建过程都在 GitHub Actions 中完成
2. **简化部署流程**：服务器只需拉取预构建的镜像
3. **版本控制**：每个镜像都有 Git SHA 标签，方便回滚
4. **自动化**：代码推送后自动构建和部署
