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
# 设置超时时间并重试
echo "尝试从 Docker Hub 拉取镜像..."
timeout 300 docker pull $DOCKER_USERNAME/my-personal-blog:latest || {
    echo "Docker Hub 拉取失败，尝试使用镜像加速器..."
    sleep 10
    timeout 300 docker pull $DOCKER_USERNAME/my-personal-blog:latest || {
        echo "镜像拉取失败，请检查网络配置"
        exit 1
    }
}

# 停止并删除旧容器
echo "停止旧容器..."
docker stop my-personal-blog 2>/dev/null || true
docker rm my-personal-blog 2>/dev/null || true

# 创建 Docker 网络
echo "创建 Docker 网络..."
docker network create app-network 2>/dev/null || true

# 启动新容器
echo "启动新容器..."
docker run -d \
  --name my-personal-blog \
  --restart always \
  --network app-network \
  -p 80:80 \
  -p 443:443 \
  -v $(pwd)/nginx.conf:/etc/nginx/conf.d/default.conf \
  -v $(pwd)/logs:/var/log/nginx \
  -v /etc/ssl/certs/www.yyi77.top.pem:/etc/nginx/ssl/www.yyi77.top.pem:ro \
  -v /etc/ssl/certs/www.yyi77.top.key:/etc/nginx/ssl/www.yyi77.top.key:ro \
  $DOCKER_USERNAME/my-personal-blog:latest

# 清理未使用的镜像
echo "清理未使用的镜像..."
docker image prune -f

echo "部署完成！"
echo "您的博客现在应该可以访问了。"
