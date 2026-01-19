package com.recording.manager.security;

/**
 * 权限不足异常
 */
public class AccessDeniedException extends RuntimeException {
    
    public AccessDeniedException(String message) {
        super(message);
    }
}
