package com.recording.manager.service;

import com.recording.manager.entity.UserRole;
import com.recording.manager.repository.UserRoleRepository;
import com.recording.manager.security.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户角色服务
 * 根据工号查询用户角色，并获取最高权限的角色
 */
@Service
public class UserRoleService {

    @Autowired
    private UserRoleRepository userRoleRepository;

    /**
     * 根据工号获取用户的最高权限角色
     * 权限优先级: ADMIN > UPLOADER > USER
     * 
     * @param employeeId 工号
     * @return 最高权限的角色，如果没有配置角色则返回 null
     */
    public Role getHighestRole(String employeeId) {
        if (employeeId == null || employeeId.isEmpty()) {
            return null;
        }

        List<UserRole> userRoles = userRoleRepository.findByEmployeeId(employeeId);
        if (userRoles.isEmpty()) {
            return null;
        }

        // 查找最高权限的角色
        Role highestRole = null;
        for (UserRole userRole : userRoles) {
            Role role = Role.fromCode(userRole.getRoleCode());
            if (role != null) {
                if (highestRole == null || getRolePriority(role) > getRolePriority(highestRole)) {
                    highestRole = role;
                }
            }
        }

        return highestRole;
    }

    /**
     * 获取角色的权限优先级
     * 数值越大权限越高
     */
    private int getRolePriority(Role role) {
        switch (role) {
            case ADMIN:
                return 3;
            case UPLOADER:
                return 2;
            case USER:
                return 1;
            default:
                return 0;
        }
    }

    /**
     * 根据工号获取用户的所有角色
     * 
     * @param employeeId 工号
     * @return 用户角色列表
     */
    public List<UserRole> getUserRoles(String employeeId) {
        return userRoleRepository.findByEmployeeId(employeeId);
    }
}
