package com.cmp.mall;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 码农天堂 —— Spring Boot 重构版启动类。
 *
 * <p>对照原版：原版靠 Tomcat 加载 web.xml 启动，Servlet 生命周期由容器管理；
 * 这里由 Spring Boot 内嵌容器接管，Bean 的创建与依赖装配交给 IoC 容器。
 */
@SpringBootApplication
@MapperScan("com.cmp.mall.mapper")
public class CmpApplication {

    public static void main(String[] args) {
        SpringApplication.run(CmpApplication.class, args);
    }
}
