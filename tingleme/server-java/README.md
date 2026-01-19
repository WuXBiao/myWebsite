# 录音管理平台 - 后端

基于 Spring Boot 的录音管理平台后端服务。

## 技术栈

- Spring Boot 2.7.0
- Java 8
- Spring Data JPA
- MySQL
- Lombok

## 功能特性

- 录音文件上传
- 录音列表管理（分页、筛选）
- 录音在线播放
- 录音删除功能

## 快速开始

### 环境要求

- Java 8+
- Maven 3.6+
- MySQL

### 安装步骤

1. 克隆项目
2. 配置数据库连接（application.properties）
3. 执行以下命令启动服务：

```bash
mvn spring-boot:run
```

服务将在 `http://localhost:8080` 启动。

### 配置说明

编辑 `src/main/resources/application.properties` 文件：

```properties
# 数据库配置
spring.datasource.url=jdbc:mysql://localhost:3306/recording_db?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=GMT%2B8
spring.datasource.username=root
spring.datasource.password=

# 文件上传配置
spring.servlet.multipart.max-file-size=100MB
spring.servlet.multipart.max-request-size=100MB

# 文件存储路径
file.upload.path=/path/to/uploads/
```

## API 接口

| 方法 | 路径 | 描述 |
|------|------|------|
| GET | /api/recordings | 获取录音列表（支持分页和筛选） |
| POST | /api/recordings/upload | 上传录音 |
| GET | /api/recordings/{id} | 获取录音详情 |
| DELETE | /api/recordings/{id} | 删除录音 |
| GET | /api/recordings/play/{id} | 播放录音 |

## 项目结构

```
src/main/java/com/recording/manager/
├── controller/     # 控制器层
├── entity/         # 实体类
├── repository/     # 数据访问层
├── service/        # 服务层
│   └── impl/       # 服务实现
└── config/         # 配置类
```

## 构建部署

```bash
# 编译打包
mvn clean package

# 运行
java -jar target/recording-manager-0.0.1-SNAPSHOT.jar
```