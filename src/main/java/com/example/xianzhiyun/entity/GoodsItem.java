package com.example.xianzhiyun.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class GoodsItem {
    private Long id;
    private Long sellerId;
    private String title;
    private String description;
    private BigDecimal price;
    private String category;
    private String itemType;
    private String coverUrls;
    private String status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer isDeleted;

    // 新增统计字段
    private Integer viewCount;
    private Integer favCount;
    private Integer chatCount;
    private Integer commentCount;
}