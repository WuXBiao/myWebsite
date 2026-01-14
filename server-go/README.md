# 后端服务

基于 Go Gin 框架的文件上传下载服务。

## 功能

- 文件上传
- 文件下载
- 文件列表查询
- 健康检查

## 安装依赖

```bash
go mod download
```

## 运行

```bash
go run main.go
```

服务将在 `http://localhost:8080` 启动。

## API 接口

### 1. 上传文件

**请求**
```
POST /api/upload
Content-Type: multipart/form-data

file: <binary file data>
```

**响应**
```json
{
  "message": "File uploaded successfully",
  "filename": "example.txt"
}
```

### 2. 下载文件

**请求**
```
GET /api/download/{filename}
```

**响应**
文件二进制内容

### 3. 获取文件列表

**请求**
```
GET /api/files
```

**响应**
```json
{
  "files": [
    {
      "name": "example.txt",
      "size": 1024
    }
  ]
}
```

### 4. 健康检查

**请求**
```
GET /api/health
```

**响应**
```json
{
  "status": "ok"
}
```

## 文件存储

上传的文件存储在 `./uploads` 目录下。

## 配置

- 服务端口: `8080`
- 最大上传文件大小: `100MB`
- 文件存储目录: `./uploads`
