package com.lms.exam.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        String jwt = "JWT";
        SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwt);
        
        Components components = new Components()
                .addSecuritySchemes(jwt, new SecurityScheme()
                        .name(jwt)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT"));

        return new OpenAPI()
                .components(components)
                .info(apiInfo())
                .addSecurityItem(securityRequirement)
                .servers(List.of(
                    new Server()
                        .url("http://172.30.1.36:8080")  // ✅ context-path 포함!
                        .description("개발 서버"),
                    new Server()
                        .url("http://localhost:8080")    // ✅ context-path 포함!
                        .description("로컬 서버")
                ));
    }

    private Info apiInfo() {
        return new Info()
                .title("LMS 시험 관리 시스템 API")
                .description("완벽한 Learning Management System API")
                .version("1.0.0");
    }
}
