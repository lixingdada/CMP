package com.cmp.mall;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot 启动类。
 */
@SpringBootApplication
@MapperScan("com.cmp.mall.mapper")
public class CmpApplication {

    public static void main(String[] args) {
        SpringApplication.run(CmpApplication.class, args);
    }
}
