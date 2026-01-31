package com.example.xianzhiyun.mapper;

import org.apache.ibatis.annotations.Mapper;
import com.example.xianzhiyun.entity.ChatMessage;
import com.example.xianzhiyun.entity.Conversation;

import java.util.List;
import java.util.Map;

@Mapper
public interface ChatMapper {

    void insertConversation(Conversation conv);

    Conversation selectConversationById(Long id);

    Long selectConversationByGoodsAndUsers(Map<String, Object> params);

    void insertConversationMember(Map<String, Object> params);

    void insertMessage(ChatMessage message);

    List<ChatMessage> selectMessagesByConv(Map<String, Object> params);

    List<ChatMessage> selectNewMessages(Map<String, Object> params);

    int countUnread(Map<String, Object> params);

    Long selectGoodsSellerId(Long id);

    String selectMemberRole(Map<String, Object> params);

    // 新增：查询会话中除指定 userId 外的另一个成员（用于一对一会话）
    // 对应 mapper XML 中的 selectConversationPartner（parameterType="map", resultType="map"）
    Map<String, Object> selectConversationPartner(Map<String, Object> params);

    List<Map<String, Object>> selectConversationsByUser(Map<String, Object> params);
}