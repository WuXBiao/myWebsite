package com.recording.manager.config;

import com.recording.manager.entity.UserRole;
import com.recording.manager.repository.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 数据初始化配置
 * 应用启动时初始化测试数据
 */
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Override
    public void run(String... args) throws Exception {
        // 如果没有数据，则初始化测试数据
        if (userRoleRepository.count() == 0) {
            // 管理员：工号 admin001，拥有管理员角色
            userRoleRepository.save(new UserRole("admin001", "admin"));
            
            // 上传者：工号 uploader001，拥有上传者角色
            userRoleRepository.save(new UserRole("uploader001", "uploader"));
            
            // 用户：工号 user001，拥有用户角色
            userRoleRepository.save(new UserRole("user001", "user"));
            
            // 多角色用户：工号 multi001，同时拥有上传者和用户角色
            userRoleRepository.save(new UserRole("multi001", "uploader"));
            userRoleRepository.save(new UserRole("multi001", "user"));
            
            System.out.println("=== 用户角色测试数据初始化完成 ===");
            System.out.println("管理员: admin001 (角色: admin)");
            System.out.println("上传者: uploader001 (角色: uploader)");
            System.out.println("用户: user001 (角色: user)");
            System.out.println("多角色: multi001 (角色: uploader, user) -> 最高权限: uploader");
        }
    }
}
