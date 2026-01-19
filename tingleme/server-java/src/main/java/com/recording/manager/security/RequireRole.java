package com.recording.manager.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义权限注解
 * 用于标注需要特定角色才能访问的方法
 * 
 * 使用示例：
 * @RequireRole({Role.ADMIN, Role.UPLOADER})
 * public void uploadFile() { ... }
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireRole {
    /**
     * 允许访问的角色列表
     * 只要用户拥有其中任一角色即可访问
     */
    Role[] value();
}
