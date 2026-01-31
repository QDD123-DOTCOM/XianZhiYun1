package com.example.xianzhiyun.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public FilterRegistrationBean<JwtFilter> jwtFilterRegistration() {
        FilterRegistrationBean<JwtFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(jwtFilter);

        // 拦截所有 /api/ 开头的请求
        registration.addUrlPatterns("/api/*");

        // 设置名称和顺序
        registration.setName("JwtFilter");
        registration.setOrder(1);

        return registration;
    }
}