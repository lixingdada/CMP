package com.cmp.mall.config;

import com.cmp.mall.security.AuthInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web 层配置：CORS 与登录拦截。
 *
 * <p>前后端分离后，开发阶段前端跑在 5173 端口、后端 8080，属于跨域；
 * 生产环境前后端同域（Nginx 统一入口、/api 反代），CORS 不会生效但也无害。
 */
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor;

    /** 允许跨域的前端来源，逗号分隔；生产环境应收窄为真实域名 */
    @Value("${cors.allowed-origins:http://localhost:5173,http://127.0.0.1:5173}")
    private String[] allowedOrigins;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/api/**")
                // 白名单：注册登录、商品浏览（未登录也能逛）
                .excludePathPatterns(
                        "/api/auth/register",
                        "/api/auth/login",
                        "/api/categories/**",
                        "/api/products/**",
                        "/api/items/**",
                        "/api/health"
                );
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOriginPatterns(allowedOrigins)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("X-Trace-Id")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
