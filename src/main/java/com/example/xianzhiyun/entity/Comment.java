package com.example.xianzhiyun.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Comment {
    private Long id;
    private Long goodsId;
    private Long userId;
    private String content;
    private Byte rating;
    private LocalDateTime createTime;
    private Long articleId;
}