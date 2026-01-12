#!/bin/bash

# 全栈部署脚本
set -e

# 检查环境变量
if [ -z "$DOCKER_USERNAME" ]; then
  echo "错误: 请设置 DOCKER_USERNAME 环境变量"
  echo "例如: export DOCKER_USERNAME=yourusername"
  exit 1
fi

echo "开始全栈部署..."

# 创建必要的目录
mkdir -p logs/nginx logs/api logs/mongodb ssl

# 拉取最新镜像
echo "拉取前端镜像..."
docker pull $DOCKER_USERNAME/my-personal-blog:latest

echo "拉取后端镜像..."
docker pull $DOCKER_USERNAME/my-blog-api:latest || echo "后端镜像不存在，跳过..."

# 停止旧容器
echo "停止旧容器..."
docker-compose -f docker-compose.prod.yml down

# 启动新容器
echo "启动新容器..."
docker-compose -f docker-compose.prod.yml up -d

# 清理未使用的镜像
echo "清理未使用的镜像..."
docker image prune -f

echo "全栈部署完成！"
echo "前端访问地址: http://your-server-ip"
echo "后端 API 地址: http://your-server-ip/api"

# 显示容器状态
echo "容器状态:"
docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"
