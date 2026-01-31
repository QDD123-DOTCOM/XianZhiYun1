package com.example.xianzhiyun;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(
        excludeName = {
                "org.springframework.boot.autoconfigure.sql.init.SqlInitializationAutoConfiguration",
                "org.springframework.boot.autoconfigure.sql.init.dependency.DatabaseInitializationDependencyAutoConfiguration"
        }
)
@MapperScan("com.example.xianzhiyun.mapper")
public class XianZhiYunApplication {
    public static void main(String[] args) {
        SpringApplication.run(XianZhiYunApplication.class, args);
    }
}