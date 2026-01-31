package com.example.xianzhiyun.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Conversation 实体 —— 对应 conversation 表
 */
@Data
public class Conversation {
    private Long id;
    private String type;       // ONE_TO_ONE 或 GROUP
    private String name;
    private Long goodsId;      // 关联商品（可为空）
    private LocalDateTime createTime;
}