package com.index.config;

import com.index.common.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 全局异常处理器
 * 统一处理 @Valid 注解抛出的验证异常以及其他常见异常
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 处理 @Valid 注解在请求体参数上验证失败的异常
     * 例如：@RequestBody @Valid User user
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        logger.warn("参数验证失败: {}", e.getMessage());
        
        // 获取所有字段错误信息
        String errorMessage = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        
        return Result.error(400, "参数验证失败: " + errorMessage);
    }

    /**
     * 处理 @Valid 注解在普通参数上验证失败的异常
     * 例如：@RequestParam @Valid String name
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public Result<Object> handleConstraintViolationException(ConstraintViolationException e) {
        logger.warn("参数验证失败: {}", e.getMessage());
        
        // 获取所有约束违反信息
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        String errorMessage = violations.stream()
                .map(violation -> {
                    String propertyPath = violation.getPropertyPath().toString();
                    // 移除方法名前缀，只保留参数名
                    if (propertyPath.contains(".")) {
                        propertyPath = propertyPath.substring(propertyPath.lastIndexOf(".") + 1);
                    }
                    return propertyPath + ": " + violation.getMessage();
                })
                .collect(Collectors.joining(", "));
        
        return Result.error(400, "参数验证失败: " + errorMessage);
    }

    /**
     * 处理 @Validated 注解在类级别验证失败的异常
     * 例如：使用 @Validated 分组验证
     */
    @ExceptionHandler(BindException.class)
    public Result<Object> handleBindException(BindException e) {
        logger.warn("对象绑定验证失败: {}", e.getMessage());
        
        String errorMessage = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        
        return Result.error(400, "绑定验证失败: " + errorMessage);
    }

    /**
     * 处理方法参数类型不匹配异常
     * 例如：期望 int 类型但传入了字符串
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public Result<Object> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        logger.warn("参数类型不匹配: {}", e.getMessage());
        
        String errorMessage = String.format("参数 '%s' 类型不匹配，期望类型: %s", 
                e.getName(), e.getRequiredType().getSimpleName());
        
        return Result.error(400, errorMessage);
    }

    /**
     * 处理空指针异常
     */
    @ExceptionHandler(NullPointerException.class)
    public Result<Object> handleNullPointerException(NullPointerException e) {
        logger.error("空指针异常", e);
        return Result.error(500, "系统内部错误");
    }

    /**
     * 处理非法参数异常
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public Result<Object> handleIllegalArgumentException(IllegalArgumentException e) {
        logger.warn("非法参数: {}", e.getMessage());
        return Result.error(400, "参数错误: " + e.getMessage());
    }

    /**
     * 处理所有未被捕获的异常（兜底处理）
     */
    @ExceptionHandler(Exception.class)
    public Result<Object> handleGenericException(Exception e) {
        logger.error("未处理的异常", e);
        return Result.error(500, "系统内部错误: " + e.getMessage());
    }
}