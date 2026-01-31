package com.example.xianzhiyun.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * ChatMessage 实体 —— 对应 chat_message 表
 */
@Data
public class ChatMessage {
    private Long id;
    private Long conversationId;
    private Long senderId;
    private String content;
    private String type;
    private String status;
    private LocalDateTime createTime;
}