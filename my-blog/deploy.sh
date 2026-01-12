#!/bin/bash

# 设置 Docker Hub 用户名
if [ -z "$DOCKER_USERNAME" ]; then
  echo "请设置 DOCKER_USERNAME 环境变量"
  echo "例如: export DOCKER_USERNAME=yourusername"
  exit 1
fi

# 创建日志目录
mkdir -p logs

# 拉取最新镜像
echo "拉取最新镜像..."
docker pull $DOCKER_USERNAME/my-personal-blog:latest

# 停止并删除旧容器
echo "停止旧容器..."
docker-compose down

# 启动新容器
echo "启动新容器..."
docker-compose up -d

# 清理未使用的镜像
echo "清理未使用的镜像..."
docker image prune -f

echo "部署完成！"
echo "您的博客现在应该可以访问了。"
