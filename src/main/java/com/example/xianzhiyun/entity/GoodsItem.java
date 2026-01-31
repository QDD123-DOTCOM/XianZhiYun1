package com.example.xianzhiyun.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 商品实体类
 */
@Data
public class GoodsItem implements Serializable {

    private Long id;
    private Long sellerId;
    private String title;
    private String description;
    private Double price;
    private String category;

    // --- 核心字段 ---
    private String coverUrls; // 封面图 URLs，逗号分隔
    private String status;    // 状态: ON_SALE, PENDING, REMOVED
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer isDeleted; // 逻辑删除标志 (0: 否, 1: 是)

    // --- 兼容旧代码字段 ---
    private String cover;      // 单张封面（可由 coverUrls 解析或前端填充）
    private String coverUrl;   // 单张封面 URL
    private String name;       // 兼容 getName()

    // --- 管理端查询所需字段（解决 MyBatis 报错的关键）---
    private String sellerNickname; // 卖家昵称，用于展示（由联表查询填充）
    // 新增字段：二级周边类型
    private String itemType;

    // getter / setter (或由 Lombok @Data 自动生成)
    public String getItemType() {
        return itemType;
    }
    public void setItemType(String itemType) {
        this.itemType = itemType;
    }
}
