package com.example.xianzhiyun.entity;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class Orders {
    private Long id;
    private String orderNo;
    private Long buyerId;
    private Double totalAmount;
    private String status; // WAIT_PAY, PAID, SHIPPED, FINISHED, CANCELLED
    private Long addressId;
    private String note;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    // 以下为新增字段，供 service 填充并返回给前端
    private List<OrderItem> items;
    private OrderAddress address;

    // 新增：当前登录用户在该订单中的角色（BUYER / SELLER / BOTH / NONE）
    private String roleForCurrentUser;
}