package com.recording.manager.security;

/**
 * 角色枚举
 * - UPLOADER: 上传者，可以上传、试听、删除录音文件
 * - USER: 用户，可以试听、下载
 * - ADMIN: 管理员，拥有所有操作权限
 */
public enum Role {
    UPLOADER("uploader", "上传者"),
    USER("user", "用户"),
    ADMIN("admin", "管理员");

    private final String code;
    private final String description;

    Role(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 根据 code 获取角色
     */
    public static Role fromCode(String code) {
        if (code == null) {
            return null;
        }
        for (Role role : values()) {
            if (role.getCode().equalsIgnoreCase(code)) {
                return role;
            }
        }
        return null;
    }
}
