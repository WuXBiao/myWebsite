#!/bin/bash

# 更新系统包
echo "正在更新系统包..."
apt-get update && apt-get upgrade -y

# 安装必要工具
echo "正在安装必要工具..."
apt-get install -y curl git vim

# 安装 Docker
echo "正在安装 Docker..."
curl -fsSL https://get.docker.com -o get-docker.sh
sh get-docker.sh

# 启动 Docker 服务
echo "正在启动 Docker 服务..."
systemctl start docker
systemctl enable docker

# 安装 Docker Compose
echo "正在安装 Docker Compose..."
curl -L "https://github.com/docker/compose/releases/download/v2.20.0/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
chmod +x /usr/local/bin/docker-compose

# 创建部署目录
mkdir -p ~/my-blog
mkdir -p ~/my-blog/logs

echo "服务器初始化完成！"
echo "请将 docker-compose.yml、nginx.conf 和 deploy.sh 上传到 ~/my-blog 目录。"
