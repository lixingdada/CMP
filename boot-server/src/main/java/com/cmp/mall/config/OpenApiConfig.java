package com.cmp.mall.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 接口文档配置。启动后访问 /swagger-ui.html 可直接调接口。
 */
@Configuration
public class OpenApiConfig {

    private static final String SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI cmpOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("码农天堂 API")
                        .version("1.0.0")
                        .description("Spring Boot 重构版：前后端分离的 RESTful 接口")
                        .contact(new Contact().name("李兴")))
                .components(new Components().addSecuritySchemes(SCHEME_NAME,
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("登录接口返回的 token，填在这里即可调试受保护接口")))
                .addSecurityItem(new SecurityRequirement().addList(SCHEME_NAME));
    }
}
