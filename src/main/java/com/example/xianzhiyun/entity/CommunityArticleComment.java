package com.example.xianzhiyun.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CommunityArticleComment {
    private Long id;
    private Long articleId; // 对应数据库 article_id
    private Long userId;    // 对应数据库 user_id
    private String content;
    private LocalDateTime createdAt; // 注意这里是 LocalDateTime

    // 用于前端展示的非数据库字段
    private String nickname;
    private String avatarUrl;
}