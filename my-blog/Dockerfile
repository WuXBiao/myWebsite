# 构建阶段
FROM node:22-alpine as build-stage

# 设置工作目录
WORKDIR /app

# 复制 package.json 和 package-lock.json
COPY package*.json ./

# 清理 npm 缓存并安装依赖（包括 devDependencies）
RUN npm cache clean --force && \
    npm install

# 调试：检查安装的包
RUN ls -la node_modules/.bin/ && \
    npm list vite && \
    which vite || echo "vite not in PATH"

# 复制项目文件
COPY . .

# 构建应用
RUN npx vite build

# 生产阶段
FROM nginx:stable-alpine as production-stage

# 从构建阶段复制构建结果到 nginx 目录
COPY --from=build-stage /app/dist /usr/share/nginx/html

# 复制自定义 nginx 配置
COPY nginx.conf /etc/nginx/conf.d/default.conf

# 暴露 80 端口
EXPOSE 80

# 启动 nginx
CMD ["nginx", "-g", "daemon off;"]
