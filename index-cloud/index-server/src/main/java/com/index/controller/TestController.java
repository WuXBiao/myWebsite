package com.index.controller;

import com.index.common.Result;
import com.index.vo.UserRequest;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 示例控制器
 * 演示 @Valid 注解异常处理
 */
@RestController
@RequestMapping("/api/test")
public class TestController {

    /**
     * 测试 @RequestBody @Valid 验证
     * POST /api/test/user
     */
    @PostMapping("/user")
    public Result<String> createUser(@Valid @RequestBody UserRequest userRequest) {
        // 如果验证通过，执行业务逻辑
        return Result.success("用户创建成功: " + userRequest.getUsername());
    }

    /**
     * 测试 @RequestParam @Valid 验证（需要使用 @Validated）
     * GET /api/test/validate-param
     */
    @GetMapping("/validate-param")
    public Result<String> validateParam(
            @RequestParam String username,
            @RequestParam Integer age) {
        
        // 手动验证（因为 @RequestParam 不能直接使用 @Valid）
        if (username == null || username.trim().isEmpty()) {
            return Result.error(400, "参数验证失败: username: 用户名不能为空");
        }
        if (username.length() < 2 || username.length() > 20) {
            return Result.error(400, "参数验证失败: username: 用户名长度必须在2-20个字符之间");
        }
        if (age == null) {
            return Result.error(400, "参数验证失败: age: 年龄不能为空");
        }
        if (age < 0 || age > 150) {
            return Result.error(400, "参数验证失败: age: 年龄必须在0-150之间");
        }
        
        return Result.success("参数验证通过: " + username + ", " + age);
    }

    /**
     * 测试路径变量验证
     * GET /api/test/user/{id}
     */
    @GetMapping("/user/{id}")
    public Result<String> getUserById(@PathVariable Long id) {
        if (id == null || id <= 0) {
            return Result.error(400, "参数验证失败: id: ID必须为正整数");
        }
        return Result.success("用户ID: " + id);
    }

    /**
     * 测试空指针异常
     * GET /api/test/null-pointer
     */
    @GetMapping("/null-pointer")
    public Result<String> testNullPointer() {
        String str = null;
        return Result.success("长度: " + str.length()); // 这里会抛出 NullPointerException
    }

    /**
     * 测试非法参数异常
     * GET /api/test/illegal-argument
     */
    @GetMapping("/illegal-argument")
    public Result<String> testIllegalArgument(@RequestParam(required = false) String type) {
        if ("error".equals(type)) {
            throw new IllegalArgumentException("测试非法参数异常");
        }
        return Result.success("参数正常");
    }
}