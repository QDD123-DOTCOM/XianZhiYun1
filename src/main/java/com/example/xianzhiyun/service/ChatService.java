package com.example.xianzhiyun.service;

import com.example.xianzhiyun.entity.ChatMessage;
import com.example.xianzhiyun.entity.Conversation;

import java.util.List;
import java.util.Map;

public interface ChatService {

    Long findConversationByGoodsAndUsers(Long goodsId, Long userA, Long userB);

    Conversation createConversation(Long goodsId, Long userA, Long userB);

    Long ensureConversation(Long goodsId, Long userA, Long userB);

    ChatMessage sendMessage(Long senderId, Long convId, String type, String content);

    List<ChatMessage> getMessages(Long convId, int offset, int limit);

    List<ChatMessage> getNewMessages(Long convId, String since);

    int countUnread(Long convId, Long userId);

    List<Map<String, Object>> listConversations(Long userId, String role);

    Conversation getConversation(Long convId);
    Map<String, Object> getConversationDetail(Long convId, Long userId);
}