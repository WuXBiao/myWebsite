package com.index;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 指标管理平台启动类
 */
@SpringBootApplication
@MapperScan("com.index.mapper")
public class IndexApplication {
    public static void main(String[] args) {
        SpringApplication.run(IndexApplication.class, args);
    }
}
