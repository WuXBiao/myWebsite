import { defineStore } from 'pinia';

const STORAGE_KEY = 'my_blog_articles';

export const useArticleStore = defineStore('article', {
  state: () => ({
    articles: [
      {
        id: '1',
        title: 'Golang 开发：语言特性与底层原理',
        category: 'fullstack',
        subcategory: 'golang',
        date: '2024-01-15',
        summary: '深入理解 Golang 语言特性，切片 slice，map 特性，底层 GMP 调度器原理',
        content: `
# Golang 开发：语言特性与底层原理

## Golang 语言特性

Go 语言是一门编译型、并发型、有垃圾回收功能的编程语言。它结合了 C 的执行速度和 Python 的易用性。

### 1. 切片（Slice）

切片是 Go 中最重要的数据结构之一，它是对数组的抽象。

\`\`\`go
// 切片声明
var s []int

// 使用 make 创建切片
s := make([]int, 5, 10)  // 长度 5，容量 10

// 切片操作
s = append(s, 1, 2, 3)   // 追加元素
subSlice := s[1:3]       // 切片操作

// 切片的底层结构
type SliceHeader struct {
    Data uintptr
    Len  int
    Cap  int
}
\`\`\`

**切片的扩容机制**：
- 当容量小于 1024 时，扩容为原来的 2 倍
- 当容量大于等于 1024 时，扩容为原来的 1.25 倍

### 2. Map 特性

Map 是 Go 中的哈希表实现，提供高效的键值对存储。

\`\`\`go
// Map 声明和初始化
m := make(map[string]int)
m["age"] = 25

// Map 的遍历（无序）
for key, value := range m {
    fmt.Println(key, value)
}

// Map 的删除
delete(m, "age")

// 检查键是否存在
if val, ok := m["age"]; ok {
    fmt.Println(val)
}
\`\`\`

**Map 的底层实现**：
- 使用哈希表实现，采用链地址法解决哈希冲突
- 初始容量为 8，当负载因子达到 6.5 时会进行扩容

## GMP 调度器原理

Go 的并发模型基于 GMP 调度器，这是 Go 高效并发的核心。

### GMP 三层模型

- **G（Goroutine）**：用户级轻量级线程，由 Go 运行时管理
- **M（Machine）**：操作系统线程，真正执行代码的实体
- **P（Processor）**：逻辑处理器，管理 G 的队列和执行上下文

\`\`\`go
// Goroutine 创建
go func() {
    fmt.Println("Running in goroutine")
}()

// 调度器会自动分配 G 到可用的 P 和 M
\`\`\`

### 调度策略

1. **本地队列调度**：P 优先执行自己队列中的 G
2. **全局队列调度**：当本地队列为空时，从全局队列获取 G
3. **工作窃取**：当本地队列为空时，可以从其他 P 的队列中窃取 G
4. **抢占式调度**：长时间运行的 G 会被抢占

这种设计使得 Go 能够高效地管理数百万个 goroutine。
        `,
      },
      {
        id: '2',
        title: '并发编程：Goroutine、Context 与 Channel',
        category: 'fullstack',
        subcategory: 'concurrency',
        date: '2024-02-10',
        summary: '熟悉 Go 并发编程，goroutine、context、sync 包及 channel 进行高效并发任务处理',
        content: `
# 并发编程：Goroutine、Context 与 Channel

## Goroutine 基础

Goroutine 是 Go 中的轻量级线程，创建成本低，可以轻松创建数百万个。

\`\`\`go
// 创建 goroutine
go func() {
    fmt.Println("Goroutine running")
}()

// 等待 goroutine 完成
var wg sync.WaitGroup
wg.Add(1)
go func() {
    defer wg.Done()
    fmt.Println("Task completed")
}()
wg.Wait()
\`\`\`

## Context 包

Context 用于管理请求的生命周期、超时和取消。

\`\`\`go
// 创建带超时的 context
ctx, cancel := context.WithTimeout(context.Background(), 5*time.Second)
defer cancel()

// 在 goroutine 中使用 context
go func(ctx context.Context) {
    select {
    case <-ctx.Done():
        fmt.Println("Context cancelled:", ctx.Err())
    case <-time.After(10 * time.Second):
        fmt.Println("Task completed")
    }
}(ctx)
\`\`\`

**Context 的四种类型**：
- Background：根 context
- WithCancel：可取消的 context
- WithTimeout：带超时的 context
- WithValue：携带值的 context

## Channel 通信

Channel 是 goroutine 之间的通信机制。

\`\`\`go
// 创建 channel
ch := make(chan int)
closedCh := make(chan int, 10)  // 缓冲 channel

// 发送和接收
go func() {
    ch <- 42  // 发送
}()
value := <-ch  // 接收

// 关闭 channel
close(closedCh)

// 遍历 channel
for val := range closedCh {
    fmt.Println(val)
}

// 使用 select 处理多个 channel
select {
case msg := <-ch1:
    fmt.Println("Received from ch1:", msg)
case msg := <-ch2:
    fmt.Println("Received from ch2:", msg)
case <-ctx.Done():
    fmt.Println("Context cancelled")
}
\`\`\`

## Sync 包

Sync 包提供了同步原语。

\`\`\`go
// WaitGroup：等待一组 goroutine 完成
var wg sync.WaitGroup
wg.Add(3)
for i := 0; i < 3; i++ {
    go func() {
        defer wg.Done()
        // 执行任务
    }()
}
wg.Wait()

// Mutex：互斥锁
var mu sync.Mutex
mu.Lock()
// 临界区代码
mu.Unlock()

// RWMutex：读写锁
var rwmu sync.RWMutex
rwmu.RLock()  // 读锁
// 读操作
rwmu.RUnlock()

// Once：只执行一次
var once sync.Once
once.Do(func() {
    fmt.Println("This runs only once")
})
\`\`\`

## 并发最佳实践

1. **使用 Context 管理生命周期**
2. **优先使用 Channel 而不是共享内存**
3. **避免数据竞争，使用 Mutex 保护共享数据**
4. **使用 WaitGroup 同步 goroutine**
5. **合理设置 goroutine 数量，避免资源耗尽**
        `,
      },
      {
        id: '3',
        title: '数据库技术：Redis、MySQL 与大数据分析',
        category: 'fullstack',
        subcategory: 'database',
        date: '2024-03-20',
        summary: '非关系型、关系型数据库及大数据分析 SQL 技术',
        content: `
# 数据库技术：Redis、MySQL 与大数据分析

## Redis 缓存技术

Redis 是高性能的内存数据库，广泛用于缓存和会话存储。

### 缓存雪崩与穿透

**缓存雪崩**：大量缓存同时失效，导致数据库被击穿。

\`\`\`go
// 解决方案：随机过期时间
func setWithRandomExpire(key string, value interface{}, baseExpire time.Duration) {
    randomExpire := baseExpire + time.Duration(rand.Intn(300))*time.Second
    rdb.Set(ctx, key, value, randomExpire)
}

// 使用本地缓存 + Redis 双层缓存
// 使用互斥锁防止缓存击穿
var mu sync.Mutex
func getWithLock(key string) interface{} {
    mu.Lock()
    defer mu.Unlock()
    // 获取缓存逻辑
}
\`\`\`

**缓存穿透**：查询不存在的数据，每次都穿透到数据库。

\`\`\`go
// 解决方案：缓存空值
func getUser(id string) *User {
    val, err := rdb.Get(ctx, "user:"+id).Result()
    if err == redis.Nil {
        // 从数据库查询
        user := db.GetUser(id)
        if user == nil {
            // 缓存空值，设置较短的过期时间
            rdb.Set(ctx, "user:"+id, "null", 1*time.Minute)
            return nil
        }
        rdb.Set(ctx, "user:"+id, user, 24*time.Hour)
        return user
    }
    // 反序列化返回
}
\`\`\`

## MySQL 数据库优化

### 事务隔离级别

\`\`\`sql
-- 四种隔离级别
-- 1. READ UNCOMMITTED：读未提交
-- 2. READ COMMITTED：读已提交
-- 3. REPEATABLE READ：可重复读（MySQL 默认）
-- 4. SERIALIZABLE：可序列化

-- 设置隔离级别
SET TRANSACTION ISOLATION LEVEL REPEATABLE READ;
\`\`\`

### 索引优化

\`\`\`sql
-- 创建索引
CREATE INDEX idx_user_email ON users(email);

-- 复合索引（遵循最左前缀原则）
CREATE INDEX idx_user_email_status ON users(email, status);

-- 查看索引使用情况
EXPLAIN SELECT * FROM users WHERE email = 'test@example.com';

-- 避免索引失效
-- 1. 避免在索引列上进行函数操作
-- 2. 避免使用 OR 连接不同列
-- 3. 避免使用 LIKE '%keyword'
-- 4. 避免隐式类型转换
\`\`\`

### 主从复制

\`\`\`sql
-- 主库配置
CHANGE MASTER TO
  MASTER_HOST='master_host',
  MASTER_USER='repl_user',
  MASTER_PASSWORD='password',
  MASTER_LOG_FILE='mysql-bin.000001',
  MASTER_LOG_POS=154;

START SLAVE;
\`\`\`

## 大数据分析

### HiveSQL 与 ODPS

\`\`\`sql
-- HiveSQL 分区表
CREATE TABLE user_events (
    user_id STRING,
    event_type STRING,
    event_time BIGINT
)
PARTITIONED BY (dt STRING)
ROW FORMAT DELIMITED FIELDS TERMINATED BY ',';

-- 分区查询
SELECT user_id, COUNT(*) as event_count
FROM user_events
WHERE dt >= '2024-01-01' AND dt <= '2024-01-31'
GROUP BY user_id;

-- ODPS 分布式查询
SELECT 
    user_id,
    COUNT(*) as pv,
    COUNT(DISTINCT session_id) as uv
FROM events
WHERE ds >= '20240101' AND ds <= '20240131'
GROUP BY user_id;
\`\`\`

### 调度配置

\`\`\`yaml
# Airflow DAG 配置示例
from airflow import DAG
from airflow.operators.bash import BashOperator

dag = DAG('daily_etl', schedule_interval='0 2 * * *')

task1 = BashOperator(
    task_id='extract_data',
    bash_command='hive -e "SELECT * FROM source_table"',
    dag=dag
)

task2 = BashOperator(
    task_id='transform_data',
    bash_command='python transform.py',
    dag=dag
)

task1 >> task2
\`\`\`
        `,
      },
      {
        id: '4',
        title: '框架与工具：Gin、Go-Zero、Gorm 实战',
        category: 'fullstack',
        subcategory: 'framework',
        date: '2024-04-15',
        summary: '掌握 Gin、Go-Zero、Gorm 框架，设计并实现复杂的业务逻辑',
        content: `
# 框架与工具：Gin、Go-Zero、Gorm 实战

## Gin 框架

Gin 是高性能的 HTTP 框架，适合构建 RESTful API。

\`\`\`go
package main

import "github.com/gin-gonic/gin"

func main() {
    r := gin.Default()
    
    // 路由定义
    r.GET("/users/:id", func(c *gin.Context) {
        id := c.Param("id")
        c.JSON(200, gin.H{
            "id": id,
            "name": "John",
        })
    })
    
    // 中间件
    r.Use(AuthMiddleware())
    
    // 路由组
    api := r.Group("/api")
    {
        api.POST("/users", CreateUser)
        api.GET("/users", ListUsers)
    }
    
    r.Run(":8080")
}

// 中间件示例
func AuthMiddleware() gin.HandlerFunc {
    return func(c *gin.Context) {
        token := c.GetHeader("Authorization")
        if token == "" {
            c.JSON(401, gin.H{"error": "Unauthorized"})
            c.Abort()
            return
        }
        c.Next()
    }
}
\`\`\`

## Gorm ORM

Gorm 是强大的 ORM 库，简化数据库操作。

\`\`\`go
import "gorm.io/gorm"

type User struct {
    ID    uint
    Name  string
    Email string
}

// 创建
db.Create(&User{Name: "John", Email: "john@example.com"})

// 查询
var user User
db.First(&user, 1)  // 按 ID 查询
db.Where("email = ?", "john@example.com").First(&user)

// 更新
db.Model(&user).Update("name", "Jane")

// 删除
db.Delete(&user)

// 关联查询
type Post struct {
    ID     uint
    Title  string
    UserID uint
    User   User
}

var posts []Post
db.Preload("User").Find(&posts)
\`\`\`

## Go-Zero 框架

Go-Zero 是高性能的微服务框架，提供完整的解决方案。

\`\`\`yaml
# api 定义文件 (user.api)
type User {
  id   int64
  name string
  email string
}

service user-api {
  @handler GetUser
  get /users/:id (GetUserRequest) returns (User)
  
  @handler CreateUser
  post /users (CreateUserRequest) returns (User)
}
\`\`\`

\`\`\`go
// 生成的处理器
func (l *GetUserLogic) GetUser(req *types.GetUserRequest) (*types.User, error) {
    // 业务逻辑
    user, err := l.svcCtx.UserModel.FindOne(l.ctx, req.Id)
    if err != nil {
        return nil, err
    }
    
    return &types.User{
        Id:    user.Id,
        Name:  user.Name,
        Email: user.Email,
    }, nil
}
\`\`\`

## 最佳实践

1. **分层架构**：handler → logic → model
2. **错误处理**：统一的错误码和错误信息
3. **日志记录**：使用结构化日志
4. **性能优化**：连接池、缓存、异步处理
5. **单元测试**：提高代码质量
        `,
      },
      {
        id: '5',
        title: '中间件技术：Kafka、MQ 与 Protobuf',
        category: 'fullstack',
        subcategory: 'middleware',
        date: '2024-05-10',
        summary: '掌握 Kafka、MQ 消息队列，Protobuf 数据序列化，token 鉴权机制设计',
        content: `
# 中间件技术：Kafka、MQ 与 Protobuf

## Kafka 消息队列

Kafka 是分布式流处理平台，用于高吞吐量的数据处理。

\`\`\`go
import "github.com/segmentio/kafka-go"

// 生产者
func produceMessage() {
    writer := &kafka.Writer{
        Addr:     kafka.TCP("localhost:9092"),
        Topic:    "events",
        Balancer: &kafka.LeastBytes{},
    }
    defer writer.Close()
    
    err := writer.WriteMessages(context.Background(),
        kafka.Message{
            Key:   []byte("user-123"),
            Value: []byte("user login event"),
        },
    )
}

// 消费者
func consumeMessage() {
    reader := kafka.NewReader(kafka.ReaderConfig{
        Brokers: []string{"localhost:9092"},
        Topic:   "events",
        GroupID: "consumer-group",
    })
    defer reader.Close()
    
    for {
        msg, err := reader.ReadMessage(context.Background())
        if err != nil {
            break
        }
        fmt.Printf("Message: %s\\n", string(msg.Value))
    }
}
\`\`\`

## RabbitMQ / ActiveMQ

\`\`\`go
import "github.com/streadway/amqp"

// 连接和声明队列
conn, _ := amqp.Dial("amqp://guest:guest@localhost:5672/")
ch, _ := conn.Channel()

q, _ := ch.QueueDeclare("task_queue", true, false, false, false, nil)

// 发送消息
ch.Publish("", q.Name, false, false, amqp.Publishing{
    ContentType: "text/plain",
    Body:        []byte("Hello World"),
})

// 消费消息
msgs, _ := ch.Consume(q.Name, "", false, false, false, false, nil)
for d := range msgs {
    fmt.Printf("Received: %s\\n", d.Body)
    d.Ack(false)
}
\`\`\`

## Protobuf 数据序列化

Protobuf 是高效的数据序列化格式，比 JSON 更紧凑。

\`\`\`protobuf
// user.proto
syntax = "proto3";

package user;

message User {
  int64 id = 1;
  string name = 2;
  string email = 3;
  repeated string tags = 4;
}

message CreateUserRequest {
  string name = 1;
  string email = 2;
}
\`\`\`

\`\`\`go
// 使用 protobuf
user := &user.User{
    Id:    123,
    Name:  "John",
    Email: "john@example.com",
    Tags:  []string{"admin", "user"},
}

// 序列化
data, _ := proto.Marshal(user)

// 反序列化
newUser := &user.User{}
proto.Unmarshal(data, newUser)
\`\`\`

## Token 鉴权机制

\`\`\`go
import "github.com/golang-jwt/jwt/v4"

// JWT Token 生成
func GenerateToken(userID int64) (string, error) {
    claims := jwt.MapClaims{
        "user_id": userID,
        "exp":     time.Now().Add(24 * time.Hour).Unix(),
    }
    
    token := jwt.NewWithClaims(jwt.SigningMethodHS256, claims)
    return token.SignedString([]byte("secret_key"))
}

// Token 验证
func VerifyToken(tokenString string) (int64, error) {
    token, err := jwt.ParseWithClaims(tokenString, &jwt.MapClaims{}, func(token *jwt.Token) (interface{}, error) {
        return []byte("secret_key"), nil
    })
    
    if claims, ok := token.Claims.(*jwt.MapClaims); ok && token.Valid {
        userID := int64((*claims)["user_id"].(float64))
        return userID, nil
    }
    return 0, err
}

// 中间件集成
func TokenMiddleware() gin.HandlerFunc {
    return func(c *gin.Context) {
        token := c.GetHeader("Authorization")
        userID, err := VerifyToken(token)
        if err != nil {
            c.JSON(401, gin.H{"error": "Invalid token"})
            c.Abort()
            return
        }
        c.Set("user_id", userID)
        c.Next()
    }
}
\`\`\`
        `,
      },
      {
        id: '6',
        title: '网络与操作系统基础',
        category: 'fullstack',
        subcategory: 'network',
        date: '2024-06-05',
        summary: '深入理解 TCP/IP、HTTP 协议和 Linux 操作系统',
        content: `
# 网络与操作系统基础

## TCP/IP 协议

### TCP 三次握手

\`\`\`
客户端                          服务器
  |                              |
  |------------ SYN ------------>|
  |                              |
  |<------- SYN-ACK -------------|
  |                              |
  |------------ ACK ------------>|
  |                              |
  |<---- 数据传输 ---->|
\`\`\`

**过程**：
1. 客户端发送 SYN 包，进入 SYN_SENT 状态
2. 服务器接收 SYN，发送 SYN-ACK，进入 SYN_RCVD 状态
3. 客户端接收 SYN-ACK，发送 ACK，进入 ESTABLISHED 状态
4. 服务器接收 ACK，进入 ESTABLISHED 状态

### TCP 四次挥手

\`\`\`
客户端                          服务器
  |                              |
  |------------ FIN ------------>|
  |                              |
  |<------------ ACK ------------|
  |                              |
  |<------------ FIN ------------|
  |                              |
  |------------ ACK ------------>|
\`\`\`

## HTTP 协议

### HTTP 请求方法

- **GET**：获取资源
- **POST**：创建资源
- **PUT**：更新资源
- **DELETE**：删除资源
- **PATCH**：部分更新资源
- **HEAD**：获取资源元数据
- **OPTIONS**：获取资源支持的方法

### HTTP 状态码

- **1xx**：信息响应
- **2xx**：成功响应（200 OK, 201 Created）
- **3xx**：重定向（301 Moved Permanently, 304 Not Modified）
- **4xx**：客户端错误（400 Bad Request, 404 Not Found）
- **5xx**：服务器错误（500 Internal Server Error, 503 Service Unavailable）

### HTTP 缓存

\`\`\`
// 强缓存
Cache-Control: max-age=3600, public
Expires: Wed, 21 Oct 2025 07:28:00 GMT

// 协商缓存
ETag: "33a64df551425fcc55e4d42a148795d9f25f89d4"
Last-Modified: Wed, 21 Oct 2024 07:28:00 GMT

// 请求头
If-None-Match: "33a64df551425fcc55e4d42a148795d9f25f89d4"
If-Modified-Since: Wed, 21 Oct 2024 07:28:00 GMT
\`\`\`

## Linux 操作系统

### 常用命令

\`\`\`bash
# 文件操作
ls -la              # 列出文件详情
cd /path            # 切换目录
mkdir dir           # 创建目录
cp file1 file2      # 复制文件
mv file1 file2      # 移动/重命名
rm file             # 删除文件

# 进程管理
ps aux              # 查看进程
top                 # 实时监控进程
kill -9 pid         # 强制杀死进程
bg/fg               # 后台/前台运行

# 网络命令
netstat -tuln       # 查看监听端口
ss -tuln            # 查看 socket 状态
ping host           # 测试连接
curl/wget           # 下载文件

# 日志查看
tail -f file        # 实时查看日志
grep pattern file   # 搜索日志
awk/sed             # 文本处理

# 打包部署
tar -czf file.tar.gz dir/   # 压缩
tar -xzf file.tar.gz        # 解压
scp file user@host:/path    # 远程复制
ssh user@host               # 远程登录
\`\`\`

### 系统监控

\`\`\`bash
# CPU 使用率
top
htop

# 内存使用
free -h
vmstat

# 磁盘使用
df -h
du -sh dir

# 网络流量
iftop
nethogs
\`\`\`
        `,
      },
      {
        id: '7',
        title: 'Go Context：请求生命周期管理',
        category: 'fullstack',
        subcategory: 'golang',
        date: '2024-07-01',
        summary: '熟练使用 Go Context 进行请求生命周期管理和取消操作',
        content: `
# Go Context：请求生命周期管理

## Context 基础

Context 是 Go 中用于管理请求生命周期、超时和取消的标准库。

\`\`\`go
import "context"

// Context 接口
type Context interface {
    Deadline() (deadline time.Time, ok bool)
    Done() <-chan struct{}
    Err() error
    Value(key interface{}) interface{}
}
\`\`\`

## Context 类型

### 1. Background Context

\`\`\`go
// 根 context，用于主函数、初始化等
ctx := context.Background()
\`\`\`

### 2. WithCancel

\`\`\`go
ctx, cancel := context.WithCancel(context.Background())
defer cancel()

go func(ctx context.Context) {
    select {
    case <-ctx.Done():
        fmt.Println("Cancelled")
    case <-time.After(5 * time.Second):
        fmt.Println("Completed")
    }
}(ctx)

// 手动取消
time.Sleep(2 * time.Second)
cancel()
\`\`\`

### 3. WithTimeout

\`\`\`go
ctx, cancel := context.WithTimeout(context.Background(), 5*time.Second)
defer cancel()

// 超时自动取消
select {
case <-ctx.Done():
    fmt.Println("Timeout or cancelled:", ctx.Err())
case result := <-doSomething(ctx):
    fmt.Println("Result:", result)
}
\`\`\`

### 4. WithDeadline

\`\`\`go
deadline := time.Now().Add(5 * time.Second)
ctx, cancel := context.WithDeadline(context.Background(), deadline)
defer cancel()
\`\`\`

### 5. WithValue

\`\`\`go
type key string

const userKey key = "user"

ctx := context.WithValue(context.Background(), userKey, "john")

// 获取值
user := ctx.Value(userKey).(string)
\`\`\`

## 实战应用

### HTTP 请求超时

\`\`\`go
func fetchWithTimeout(url string, timeout time.Duration) (string, error) {
    ctx, cancel := context.WithTimeout(context.Background(), timeout)
    defer cancel()
    
    req, _ := http.NewRequestWithContext(ctx, "GET", url, nil)
    resp, err := http.DefaultClient.Do(req)
    if err != nil {
        return "", err
    }
    defer resp.Body.Close()
    
    body, _ := io.ReadAll(resp.Body)
    return string(body), nil
}
\`\`\`

### 数据库查询超时

\`\`\`go
func queryWithTimeout(ctx context.Context, query string) (*sql.Rows, error) {
    ctx, cancel := context.WithTimeout(ctx, 5*time.Second)
    defer cancel()
    
    return db.QueryContext(ctx, query)
}
\`\`\`

### Goroutine 协调

\`\`\`go
func processWithCancel(ctx context.Context, items []string) {
    ctx, cancel := context.WithCancel(ctx)
    defer cancel()
    
    var wg sync.WaitGroup
    for _, item := range items {
        wg.Add(1)
        go func(item string) {
            defer wg.Done()
            select {
            case <-ctx.Done():
                fmt.Println("Cancelled:", item)
            default:
                // 处理 item
                fmt.Println("Processing:", item)
            }
        }(item)
    }
    
    wg.Wait()
}
\`\`\`

## 最佳实践

1. **不要在结构体中存储 Context**
2. **Context 应该作为函数的第一个参数**
3. **使用 context.WithCancel 实现优雅关闭**
4. **合理设置超时时间，避免过长或过短**
5. **使用 context.WithValue 传递请求相关的值**
6. **总是 defer cancel() 释放资源**
        `,
      },
      {
        id: '8',
        title: '开发工具与团队协作',
        category: 'fullstack',
        subcategory: 'tools',
        date: '2024-08-15',
        summary: '熟练使用 Git、AI 编辑器、IDE 和团队协作工具',
        content: `
# 开发工具与团队协作

## Git 版本控制

### 基本命令

\`\`\`bash
# 初始化仓库
git init
git clone <url>

# 提交流程
git add .
git commit -m "feat: add new feature"
git push origin main

# 分支管理
git branch feature/new-feature
git checkout feature/new-feature
git merge feature/new-feature

# 查看历史
git log --oneline
git diff HEAD~1
git show <commit>
\`\`\`

### 工作流规范

**Conventional Commits**：
\`\`\`
<type>(<scope>): <subject>

<body>

<footer>
\`\`\`

类型：
- **feat**：新功能
- **fix**：bug 修复
- **docs**：文档
- **style**：代码风格
- **refactor**：重构
- **perf**：性能优化
- **test**：测试
- **chore**：构建、依赖等

### 高级技巧

\`\`\`bash
# 变基
git rebase main

# 交互式变基
git rebase -i HEAD~3

# 暂存
git stash
git stash pop

# 撤销
git reset --soft HEAD~1
git revert <commit>

# 标签
git tag v1.0.0
git push origin v1.0.0
\`\`\`

## AI 编辑器

### Cursor

Cursor 是基于 VS Code 的 AI 编辑器，提供智能代码补全和生成。

**快捷键**：
- Ctrl+K：AI 代码生成
- Ctrl+L：AI 聊天
- Ctrl+Shift+K：删除行

### Windsurf

Windsurf 是新一代 AI 编辑器，提供更强大的代码理解和生成能力。

**特性**：
- 自然语言编程
- 多文件编辑
- 实时代码分析

## IDE 配置

### GoLand

\`\`\`
// 推荐配置
- 启用 gofmt 自动格式化
- 配置 golangci-lint 检查
- 启用 go mod 支持
- 配置调试器
\`\`\`

### VS Code

\`\`\`json
{
  "go.useLanguageServer": true,
  "go.lintOnSave": "package",
  "go.lintTool": "golangci-lint",
  "editor.formatOnSave": true,
  "[go]": {
    "editor.defaultFormatter": "golang.go"
  }
}
\`\`\`

## 团队协作

### 代码审查

\`\`\`
1. 创建 Pull Request
2. 请求代码审查
3. 解决审查意见
4. 合并到主分支
\`\`\`

### 持续集成

\`\`\`yaml
# GitHub Actions
name: CI

on: [push, pull_request]

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - uses: actions/setup-go@v2
      - run: go test ./...
      - run: go vet ./...
\`\`\`

### 文档维护

\`\`\`markdown
# 项目文档结构
- README.md：项目概述
- CONTRIBUTING.md：贡献指南
- docs/：详细文档
- examples/：示例代码
\`\`\`

## 最佳实践

1. **定期拉取最新代码**
2. **创建特性分支进行开发**
3. **编写有意义的提交信息**
4. **进行代码审查**
5. **使用 CI/CD 自动化测试**
6. **保持代码风格一致**
7. **及时更新文档**
        `,
      },
    ],
    categories: [
      { id: 'fullstack', name: '全栈', icon: 'layers' },
      { id: 'article', name: '文章', icon: 'file-text' },
    ],
  }),
  actions: {
    init() {
      try {
        const raw = localStorage.getItem(STORAGE_KEY);
        if (!raw) {
          localStorage.setItem(STORAGE_KEY, JSON.stringify(this.articles));
          return;
        }
        const parsed = JSON.parse(raw);
        if (Array.isArray(parsed)) {
          this.articles = parsed;
        }
      } catch (e) {
        localStorage.setItem(STORAGE_KEY, JSON.stringify(this.articles));
      }
    },
    upsertArticle(article) {
      if (!article || !article.id) return;
      const idx = this.articles.findIndex(a => a.id === article.id);
      if (idx >= 0) {
        this.articles[idx] = { ...this.articles[idx], ...article };
      } else {
        this.articles.unshift(article);
      }
      localStorage.setItem(STORAGE_KEY, JSON.stringify(this.articles));
    }
  },
  getters: {
    getArticleById: (state) => (id) => {
      return state.articles.find(article => article.id === id);
    },
    getArticlesByCategory: (state) => (category) => {
      return state.articles.filter(article => article.category === category);
    },
    getAllArticles: (state) => {
      return state.articles;
    },
    getCategories: (state) => {
      return state.categories;
    }
  }
});
