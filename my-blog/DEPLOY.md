# 前端博客部署指南

本文档提供将前端博客项目通过 GitHub Actions 和 Docker Hub 部署到阿里云服务器的详细步骤。

> **注意**: GitHub Actions 工作流现在位于仓库根目录 `myWebSite/.github/workflows/` 下，可以管理整个项目的 CI/CD 流程。

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
# 方案1：使用 DaoCloud 镜像安装 Docker（推荐）
curl -sSL https://get.daocloud.io/docker | sh

# 方案2：如果方案1失败，使用包管理器安装（CentOS/RHEL）
# sudo yum install -y yum-utils
# sudo yum-config-manager --add-repo http://mirrors.aliyun.com/docker-ce/linux/centos/docker-ce.repo
# sudo yum install -y docker-ce docker-ce-cli containerd.io

# 方案3：Ubuntu/Debian 系统使用包管理器
# sudo apt-get update
# sudo apt-get install -y apt-transport-https ca-certificates curl gnupg lsb-release
# curl -fsSL http://mirrors.aliyun.com/docker-ce/linux/ubuntu/gpg | sudo gpg --dearmor -o /usr/share/keyrings/docker-archive-keyring.gpg
# echo "deb [arch=amd64 signed-by=/usr/share/keyrings/docker-archive-keyring.gpg] http://mirrors.aliyun.com/docker-ce/linux/ubuntu $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
# sudo apt-get update
# sudo apt-get install -y docker-ce docker-ce-cli containerd.io

# 启动 Docker 服务
sudo systemctl start docker
sudo systemctl enable docker

# 配置 Docker 镜像加速器（阿里云）
sudo mkdir -p /etc/docker
sudo tee /etc/docker/daemon.json <<-'EOF'
{
  "registry-mirrors": ["https://mirror.ccs.tencentyun.com"]
}
EOF
sudo systemctl daemon-reload
sudo systemctl restart docker

# 安装 Docker Compose
# 方案1：使用 EPEL 仓库（推荐）
sudo yum install -y epel-release
sudo yum install -y docker-compose

# 方案2：如果 EPEL 没有，使用 pip 安装较老版本
# sudo yum install -y python3-pip
# sudo pip3 install docker-compose==1.29.2

# 方案3：直接下载二进制文件
# sudo curl -L "https://github.com/docker/compose/releases/download/v2.20.0/docker-compose-linux-x86_64" -o /usr/local/bin/docker-compose
# sudo chmod +x /usr/local/bin/docker-compose

# 验证安装
docker --version
docker-compose --version
```

#### 2.2 准备部署文件

在服务器上创建一个目录用于部署：

```bash
mkdir -p ~/my-blog
cd ~/my-blog
```

只需要将以下文件上传到服务器：

1. `nginx.conf`

### 3. 部署流程

推送代码到 GitHub 的 main 分支后，GitHub Actions 会自动：
1. 构建 Docker 镜像并推送到 Docker Hub
2. SSH 连接到服务器拉取最新镜像
3. 重启容器完成部署

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

1. 修改 GitHub Actions 流水线，添加 443 端口映射
2. 更新 Nginx 配置以支持 HTTPS
3. 使用 Certbot 获取 SSL 证书

## 简历文件配置

简历文件不存放在 GitHub 仓库中，而是直接上传到服务器。

### 服务器配置

```bash
# 创建简历目录
sudo mkdir -p /data/resume
sudo chmod 755 /data/resume

# 上传简历文件
scp your-resume.pdf user@your-server:/data/resume/resume.pdf
```

Docker 容器会自动将 `/data/resume` 挂载到网站的 `/resume/` 路径，无需额外配置。

### 更新简历

只需将新简历上传到服务器即可，无需重启容器：

```bash
scp new-resume.pdf user@your-server:/data/resume/resume.pdf
```

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
