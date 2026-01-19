# 录音管理平台

这是一个基于 Spring Boot 和 Vue.js 的录音管理平台，用户可以上传、试听和管理录音文件。

## 功能特性

- 用户可以上传录音文件
- 录音列表展示（支持分页）
- 支持按标题和上传者筛选录音
- 录音在线试听功能
- 录音删除功能

## 技术栈

### 后端 (server-java)
- Spring Boot 2.7.0
- Java 8
- Spring Data JPA
- MySQL
- Lombok

### 前端 (server-vue)
- Vue 3
- Vue Router
- Element Plus UI 组件库
- Axios HTTP 客户端

## 快速开始

### 后端启动

1. 确保已安装 Java 8 和 Maven
2. 配置数据库连接（application.properties）
3. 运行以下命令启动后端服务：

```bash
cd /Users/xubiaowu/projects/myWebsite/tingleme/server-java
mvn spring-boot:run
```

后端服务将在 `http://localhost:8080` 启动。

### 前端启动

1. 确保已安装 Node.js
2. 运行以下命令启动前端开发服务器：

```bash
cd /Users/xubiaowu/projects/myWebsite/tingleme/server-vue
npm install
npm run serve
```

前端服务将在 `http://localhost:8080` 启动。

## API 接口

### 录音管理

- `GET /api/recordings` - 获取录音列表（支持分页和筛选）
- `POST /api/recordings/upload` - 上传录音
- `DELETE /api/recordings/{id}` - 删除录音
- `GET /api/recordings/{id}` - 获取录音详情
- `GET /api/recordings/play/{id}` - 播放录音

## 项目结构

```
tingleme/
├── server-java/          # 后端 Spring Boot 项目
│   ├── src/main/java/com/recording/manager/
│   │   ├── controller/   # 控制器层
│   │   ├── entity/       # 实体类
│   │   ├── repository/   # 数据访问层
│   │   ├── service/      # 服务层
│   │   └── config/       # 配置类
│   └── pom.xml
└── server-vue/           # 前端 Vue 项目
    ├── src/
    │   ├── api/          # API 服务
    │   ├── components/   # 组件
    │   ├── views/        # 页面视图
    │   ├── router/       # 路由配置
    │   └── assets/       # 静态资源
    └── package.json
```

## 配置说明

### 后端配置

编辑 `server-java/src/main/resources/application.properties` 文件配置数据库连接：

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/recording_db?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=GMT%2B8
spring.datasource.username=root
spring.datasource.password=
```

### 前端配置

API 地址在 `server-vue/src/api/recordingApi.js` 中配置：

```javascript
baseURL: 'http://localhost:8080/api'
```

## 部署说明

### 生产环境部署

1. 构建后端 JAR 包：
```bash
cd /Users/xubiaowu/projects/myWebsite/tingleme/server-java
mvn clean package
java -jar target/recording-manager-0.0.1-SNAPSHOT.jar
```

2. 构建前端静态文件：
```bash
cd /Users/xubiaowu/projects/myWebsite/tingleme/server-vue
npm run build
```

构建后的文件在 `dist/` 目录，可部署到 Nginx 或其他 Web 服务器。