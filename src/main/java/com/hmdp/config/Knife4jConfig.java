package com.hmdp.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Volunteer
 * @title
 * @description
 */
@Configuration
public class Knife4jConfig {

    @Bean
    public GroupedOpenApi hmdpApi() {
        return GroupedOpenApi.builder()
                .group("hmdp")
                // 指定 Controller 扫描包路径
                .packagesToScan("com.hmdp.controller")
                .pathsToMatch("/**")
                .build();
    }

    @Bean
    public OpenAPI openApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("黑马点评")
                        .description("接口文档")
                        .version("v1.0"));
    }
}
