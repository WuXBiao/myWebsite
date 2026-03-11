# @Valid 注解异常处理测试指南

## 概述

这个全局异常处理器可以捕获 `@Valid` 注解抛出的所有验证异常，包括：
- `MethodArgumentNotValidException` - `@RequestBody @Valid` 验证失败
- `ConstraintViolationException` - `@RequestParam @Valid` 验证失败
- `BindException` - 对象绑定验证失败
- `MethodArgumentTypeMismatchException` - 参数类型不匹配
- `NullPointerException` - 空指针异常
- `IllegalArgumentException` - 非法参数异常

## 启动服务

```bash
cd /Users/xubiaowu/projects/myWebsite/index-cloud/index-server
mvn spring-boot:run
```

服务默认运行在 `http://localhost:8080`

## 测试用例

### 1. 测试 @RequestBody @Valid 验证失败

```bash
# 测试完整错误数据
curl -X POST http://localhost:8080/api/test/user \
  -H "Content-Type: application/json" \
  -d '{}'

# 期望返回：
# {"code":400,"message":"参数验证失败: username: 用户名不能为空, password: 密码不能为空, age: 年龄不能为空, phone: 手机号不能为空","data":null}
```

```bash
# 测试部分错误数据
curl -X POST http://localhost:8080/api/test/user \
  -H "Content-Type: application/json" \
  -d '{
    "username": "a",
    "password": "123",
    "age": -5,
    "email": "invalid-email",
    "phone": "123"
  }'

# 期望返回：
# {"code":400,"message":"参数验证失败: username: 用户名长度必须在2-20个字符之间, password: 密码长度至少6位, age: 年龄不能为空, email: 邮箱格式不正确, phone: 手机号必须为11位","data":null}
```

```bash
# 测试正确数据
curl -X POST http://localhost:8080/api/test/user \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123",
    "age": 25,
    "email": "test@example.com",
    "phone": "13800138000"
  }'

# 期望返回：
# {"code":200,"message":"用户创建成功: testuser","data":null}
```

### 2. 测试参数验证

```bash
# 测试缺失参数
curl "http://localhost:8080/api/test/validate-param"

# 期望返回：
# {"code":400,"message":"参数验证失败: username: 用户名不能为空","data":null}
```

```bash
# 测试参数格式错误
curl "http://localhost:8080/api/test/validate-param?username=a&age=abc"

# 期望返回参数类型不匹配的错误
```

### 3. 测试路径变量验证

```bash
# 测试无效ID
curl "http://localhost:8080/api/test/user/0"

# 期望返回：
# {"code":400,"message":"参数验证失败: id: ID必须为正整数","data":null}
```

```bash
# 测试有效ID
curl "http://localhost:8080/api/test/user/123"

# 期望返回：
# {"code":200,"message":"用户ID: 123","data":null}
```

### 4. 测试空指针异常

```bash
curl "http://localhost:8080/api/test/null-pointer"

# 期望返回：
# {"code":500,"message":"系统内部错误","data":null}
```

### 5. 测试非法参数异常

```bash
curl "http://localhost:8080/api/test/illegal-argument?type=error"

# 期望返回：
# {"code":400,"message":"参数错误: 测试非法参数异常","data":null}
```

## 异常处理类说明

### GlobalExceptionHandler.java

这个类使用 `@RestControllerAdvice` 注解，会对所有 `@RestController` 进行全局异常处理。

主要处理的异常类型：

1. **MethodArgumentNotValidException**
   - 处理 `@RequestBody @Valid` 验证失败
   - 提取所有字段的错误信息并格式化返回

2. **ConstraintViolationException**
   - 处理 `@RequestParam @Valid` 验证失败
   - 处理路径变量验证失败
   - 格式化约束违反信息

3. **BindException**
   - 处理对象绑定验证失败
   - 通常用于表单数据绑定

4. **MethodArgumentTypeMismatchException**
   - 处理参数类型不匹配
   - 例如：期望 int 但传入字符串

5. **NullPointerException**
   - 处理空指针异常
   - 返回统一的内部错误信息

6. **IllegalArgumentException**
   - 处理非法参数异常
   - 返回具体的错误信息

7. **Exception** (兜底处理)
   - 处理所有未被捕获的异常
   - 记录详细日志并返回通用错误信息

## 使用建议

1. **在 DTO 中使用验证注解**：
   ```java
   public class UserRequest {
       @NotBlank(message = "用户名不能为空")
       private String username;
       
       @Min(value = 18, message = "年龄必须大于18")
       private Integer age;
   }
   ```

2. **在控制器中使用 @Valid**：
   ```java
   @PostMapping("/users")
   public Result<String> createUser(@Valid @RequestBody UserRequest request) {
       // 业务逻辑
   }
   ```

3. **自定义验证消息**：
   - 为每个验证注解提供清晰的中文错误消息
   - 便于前端用户理解

4. **日志记录**：
   - 所有异常都会记录到日志中
   - 便于问题排查和监控

5. **统一返回格式**：
   - 使用 `Result` 类统一封装返回数据
   - 便于前端统一处理

## 扩展建议

如果需要更复杂的验证逻辑，可以：

1. 创建自定义验证注解
2. 实现 `ConstraintValidator` 接口
3. 在全局异常处理器中添加对应的处理方法