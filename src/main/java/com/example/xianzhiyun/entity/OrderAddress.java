package com.example.xianzhiyun.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class OrderAddress {
    private Long id;
    private Long buyerId;
    private String name;
    private String mobile;
    private String school;
    private String region;
    private String detail;
    private LocalDateTime createTime;
}