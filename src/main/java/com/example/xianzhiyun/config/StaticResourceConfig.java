package com.example.xianzhiyun.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class StaticResourceConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 把 URL 路径 /static/upload/** 映射到磁盘目录 D:/ESJYPT/tp/upload/
        registry.addResourceHandler("/static/upload/**")
                .addResourceLocations("file:D:/ESJYPT/tp/upload/");
    }
}