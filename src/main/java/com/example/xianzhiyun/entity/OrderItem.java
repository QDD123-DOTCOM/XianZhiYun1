package com.example.xianzhiyun.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderItem {
    private Long id;
    private Long orderId;
    private Long goodsId;
    private Long sellerId;
    private Double price;
    private Integer quantity;
    private Double amount;
    // 是否已收货：0/1 或 null
    private Integer received;
    private LocalDateTime receiveTime;

    // 发货相关字段
    private Integer shipped; // 0/1
    private LocalDateTime shipTime;
    private String expressCompany;
    private String trackingNumber;
    private String shipProof;

    // 新增用于返回给前端展示的字段（与 Mapper 中的 alias 对应）
    private String title;       // 商品标题（来自 goods_item.title）
    private String cover;       // 方便前端直接读的单图字段（由 coverUrls 的第一个填充）
    private String coverUrls;   // 多图字段（逗号分隔），来自 goods_item.cover_urls
    private String description; // 商品描述
}