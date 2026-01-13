#!/bin/bash

# 检查参数
if [ $# -lt 1 ]; then
  echo "用法: $0 <服务器IP或域名> [用户名]"
  echo "例如: $0 192.168.1.100 root"
  exit 1
fi

SERVER_IP=$1
USER=${2:-root}

# 要上传的文件
FILES=(
  "nginx.conf"
  "deploy.sh"
  ".env.example"
)

echo "正在上传必要文件到 $USER@$SERVER_IP:~/my-blog/"

# 上传文件
for file in "${FILES[@]}"; do
  echo "上传 $file..."
  scp "$file" "$USER@$SERVER_IP:~/my-blog/"
done

echo "文件上传完成！"
echo "请在服务器上执行以下命令："
echo "  cd ~/my-blog"
echo "  cp .env.example .env  # 修改 .env 文件设置您的 Docker Hub 用户名"
echo "  chmod +x deploy.sh"
echo "  ./deploy.sh"
