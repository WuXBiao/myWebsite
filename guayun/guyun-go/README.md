# 卦云|运势预测 - 后端服务

基于 Go-Zero 框架的运势预测后端服务。

## 功能特性

- 八字解析和计算
- 五行分析
- 长期/短期运势推测
- 用户数据管理
- 历史记录查询

## 项目结构

```
guyun-go/
├── main.go           # 主程序入口
├── config.yaml       # 配置文件
├── go.mod            # Go 模块定义
└── README.md         # 项目文档
```

## API 接口

### 1. 健康检查
```
GET /api/health
```

### 2. 运势预测
```
POST /api/fortune/predict

Request:
{
  "year": 1990,
  "month": 5,
  "day": 15,
  "hour": 14,
  "minute": 30,
  "gender": "male"
}

Response:
{
  "code": 0,
  "message": "success",
  "data": {
    "bazi": {...},
    "fortune": {...}
  }
}
```

### 3. 获取历史记录
```
GET /api/fortune/history/:userId
```

### 4. 获取运势详情
```
GET /api/fortune/detail/:recordId
```

## 运行

```bash
go run main.go
```

服务将在 `http://localhost:8888` 启动。

## 后续开发计划

- [ ] 集成数据库（MySQL/PostgreSQL）
- [ ] 完善八字计算算法
- [ ] 添加用户认证
- [ ] 实现缓存机制
- [ ] 添加日志系统
- [ ] 性能优化
