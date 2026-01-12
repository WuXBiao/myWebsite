#!/bin/bash

# 仅前端部署脚本
set -e

# 检查环境变量
if [ -z "$DOCKER_USERNAME" ]; then
  echo "错误: 请设置 DOCKER_USERNAME 环境变量"
  echo "例如: export DOCKER_USERNAME=yourusername"
  exit 1
fi

echo "开始前端部署..."

# 创建必要的目录
mkdir -p logs/nginx

# 拉取最新前端镜像
echo "拉取前端镜像..."
docker pull $DOCKER_USERNAME/my-personal-blog:latest

# 停止并重启前端容器
echo "重启前端容器..."
docker stop my-personal-blog || true
docker rm my-personal-blog || true

# 启动前端容器
docker run -d \
  --name my-personal-blog \
  --restart always \
  -p 80:80 \
  -v $(pwd)/nginx.conf:/etc/nginx/conf.d/default.conf \
  -v $(pwd)/logs/nginx:/var/log/nginx \
  $DOCKER_USERNAME/my-personal-blog:latest

# 清理未使用的镜像
echo "清理未使用的镜像..."
docker image prune -f

echo "前端部署完成！"
echo "访问地址: http://your-server-ip"

# 显示容器状态
echo "容器状态:"
docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}" | grep my-personal-blog
