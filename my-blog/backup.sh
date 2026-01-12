#!/bin/bash

# 设置备份目录
BACKUP_DIR="/root/backups"
BACKUP_NAME="my-blog-$(date +%Y%m%d-%H%M%S).tar.gz"
PROJECT_DIR="$(pwd)"

# 创建备份目录（如果不存在）
mkdir -p $BACKUP_DIR

# 创建备份
echo "正在创建备份..."
tar -czf $BACKUP_DIR/$BACKUP_NAME $PROJECT_DIR

# 只保留最近 5 个备份
echo "清理旧备份..."
ls -tp $BACKUP_DIR/*.tar.gz | grep -v '/$' | tail -n +6 | xargs -I {} rm -- {}

echo "备份完成！备份文件: $BACKUP_DIR/$BACKUP_NAME"
