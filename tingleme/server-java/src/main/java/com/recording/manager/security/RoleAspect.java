package com.recording.manager.security;

import com.recording.manager.service.UserRoleService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * 角色权限切面
 * 拦截带有 @RequireRole 注解的方法，验证用户角色
 * 通过请求头 X-Employee-Id 获取工号，查询用户角色
 */
@Aspect
@Component
public class RoleAspect {

    @Autowired
    private UserRoleService userRoleService;

    /**
     * 环绕通知：拦截所有带有 @RequireRole 注解的方法
     */
    @Around("@annotation(com.recording.manager.security.RequireRole)")
    public Object checkRole(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取方法上的注解
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RequireRole requireRole = method.getAnnotation(RequireRole.class);
        
        if (requireRole == null) {
            return joinPoint.proceed();
        }
        
        // 获取允许访问的角色列表
        Role[] allowedRoles = requireRole.value();
        
        // 获取当前用户的最高权限角色
        Role currentRole = getCurrentRole();
        
        // 管理员拥有所有权限
        if (currentRole == Role.ADMIN) {
            return joinPoint.proceed();
        }
        
        // 检查当前角色是否在允许的角色列表中
        boolean hasPermission = Arrays.asList(allowedRoles).contains(currentRole);
        
        if (!hasPermission) {
            String employeeId = getEmployeeId();
            throw new AccessDeniedException("权限不足，需要角色: " + Arrays.toString(allowedRoles) + 
                    "，当前工号: " + employeeId + "，当前角色: " + currentRole);
        }
        
        return joinPoint.proceed();
    }
    
    /**
     * 从请求头中获取工号，然后查询用户的最高权限角色
     * 请求头名称: X-Employee-Id
     */
    private Role getCurrentRole() {
        String employeeId = getEmployeeId();
        if (employeeId == null || employeeId.isEmpty()) {
            return null;
        }
        
        return userRoleService.getHighestRole(employeeId);
    }
    
    /**
     * 从请求头中获取工号
     */
    private String getEmployeeId() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return null;
        }
        
        HttpServletRequest request = attributes.getRequest();
        return request.getHeader("X-Employee-Id");
    }
}
