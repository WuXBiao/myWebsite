package com.recording.manager.repository;

import com.recording.manager.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 用户角色数据访问层
 */
@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    
    /**
     * 根据工号查询用户的所有角色
     * @param employeeId 工号
     * @return 用户角色列表
     */
    List<UserRole> findByEmployeeId(String employeeId);
}
