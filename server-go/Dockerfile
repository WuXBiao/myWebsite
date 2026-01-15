# 构建阶段
FROM golang:1.21-alpine AS build-stage

WORKDIR /app

# 复制 go.mod 和 go.sum
COPY go.mod go.sum ./

# 下载依赖
RUN go mod download

# 复制源代码
COPY . .

# 构建应用
RUN CGO_ENABLED=0 GOOS=linux go build -a -installsuffix cgo -o server .

# 生产阶段
FROM alpine:latest

# 安装 ca-certificates 用于 HTTPS
RUN apk --no-cache add ca-certificates

WORKDIR /app

# 从构建阶段复制二进制文件
COPY --from=build-stage /app/server .

# 创建 uploads 目录
RUN mkdir -p /app/uploads

# 暴露 8080 端口
EXPOSE 8080

# 启动应用
CMD ["./server"]
