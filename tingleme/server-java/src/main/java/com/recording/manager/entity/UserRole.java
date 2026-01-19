package com.recording.manager.entity;

import javax.persistence.*;

/**
 * 用户角色实体
 * 存储工号和角色的对应关系
 * 一个用户可以有多个角色
 */
@Entity
@Table(name = "user_roles")
public class UserRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 工号
     */
    @Column(name = "employee_id", nullable = false)
    private String employeeId;

    /**
     * 角色编码: uploader, user, admin
     */
    @Column(name = "role_code", nullable = false)
    private String roleCode;

    public UserRole() {
    }

    public UserRole(String employeeId, String roleCode) {
        this.employeeId = employeeId;
        this.roleCode = roleCode;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }
}
