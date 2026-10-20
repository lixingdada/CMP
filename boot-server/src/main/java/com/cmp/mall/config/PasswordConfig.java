package com.cmp.mall.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 密码编码器。
 *
 * <p>原版把密码明文存进 {@code signon.password}，登录时用 SQL 直接比对
 * （{@code WHERE username = ? AND password = ?}）——一旦库泄露，所有用户密码即刻可见，
 * 而且相同密码在库里长得一样。
 *
 * <p>BCrypt 每次哈希都会生成随机盐并混入结果，因此同一密码两次入库的哈希值不同；
 * 校验时用 {@code matches(raw, encoded)} 反推，不需要单独存盐字段。
 */
@Configuration
public class PasswordConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        // strength 10 是默认值，约 50~80ms/次，兼顾安全与登录性能
        return new BCryptPasswordEncoder(10);
    }
}
