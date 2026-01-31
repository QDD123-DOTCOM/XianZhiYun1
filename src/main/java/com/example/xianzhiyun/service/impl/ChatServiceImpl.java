package com.example.xianzhiyun.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.xianzhiyun.entity.ChatMessage;
import com.example.xianzhiyun.entity.Conversation;
import com.example.xianzhiyun.mapper.ChatMapper;
import com.example.xianzhiyun.service.ChatService;

import java.time.LocalDateTime;
import java.util.*;

/**
 * ChatServiceImpl - 最小可用实现（含买家/卖家角色处理）
 */
@Service
public class ChatServiceImpl implements ChatService {

    @Autowired
    private ChatMapper chatMapper;

    @Override
    public Long findConversationByGoodsAndUsers(Long goodsId, Long userA, Long userB) {
        if (goodsId == null) return null;
        Map<String,Object> params = new HashMap<>();
        params.put("goodsId", goodsId);
        params.put("userA", userA);
        params.put("userB", userB);
        Long convId = chatMapper.selectConversationByGoodsAndUsers(params);
        return convId;
    }

    @Override
    @Transactional
    public Conversation createConversation(Long goodsId, Long userA, Long userB) {
        Conversation c = new Conversation();
        c.setType("ONE_TO_ONE");
        c.setName("会话");
        c.setGoodsId(goodsId);
        c.setCreateTime(LocalDateTime.now());
        chatMapper.insertConversation(c);
        Long convId = c.getId();

        // 如果与商品相关，尝试把卖家/买家写入 conversation_member
        if (goodsId != null) {
            Long sellerId = chatMapper.selectGoodsSellerId(goodsId);
            if (sellerId != null) {
                if (Objects.equals(sellerId, userA)) {
                    // userA 为 SELLER -> 按 OWNER / MEMBER 写入
                    Map<String,Object> m1 = new HashMap<>();
                    m1.put("convId", convId);
                    m1.put("userId", userA);
                    m1.put("role", "OWNER");
                    chatMapper.insertConversationMember(m1);

                    Map<String,Object> m2 = new HashMap<>();
                    m2.put("convId", convId);
                    m2.put("userId", userB);
                    m2.put("role", "MEMBER");
                    chatMapper.insertConversationMember(m2);
                } else if (Objects.equals(sellerId, userB)) {
                    // userB 为 SELLER
                    Map<String,Object> m1 = new HashMap<>();
                    m1.put("convId", convId);
                    m1.put("userId", userA);
                    m1.put("role", "MEMBER");
                    chatMapper.insertConversationMember(m1);

                    Map<String,Object> m2 = new HashMap<>();
                    m2.put("convId", convId);
                    m2.put("userId", userB);
                    m2.put("role", "OWNER");
                    chatMapper.insertConversationMember(m2);
                } else {
                    // 两方都不是卖家：双方都记为 MEMBER
                    Map<String,Object> m1 = new HashMap<>();
                    m1.put("convId", convId);
                    m1.put("userId", userA);
                    m1.put("role", "MEMBER");
                    chatMapper.insertConversationMember(m1);

                    Map<String,Object> m2 = new HashMap<>();
                    m2.put("convId", convId);
                    m2.put("userId", userB);
                    m2.put("role", "MEMBER");
                    chatMapper.insertConversationMember(m2);
                }
            } else {
                // 未能通过 goodsId 找到 seller
                Map<String,Object> m1 = new HashMap<>();
                m1.put("convId", convId);
                m1.put("userId", userA);
                m1.put("role", "MEMBER");
                chatMapper.insertConversationMember(m1);

                Map<String,Object> m2 = new HashMap<>();
                m2.put("convId", convId);
                m2.put("userId", userB);
                m2.put("role", "MEMBER");
                chatMapper.insertConversationMember(m2);
            }
        } else {
            // 非商品会话：创建者为 MEMBER，另一方 MEMBER 或按业务需求调整
            Map<String,Object> m1 = new HashMap<>();
            m1.put("convId", convId);
            m1.put("userId", userA);
            m1.put("role", "MEMBER");
            chatMapper.insertConversationMember(m1);

            Map<String,Object> m2 = new HashMap<>();
            m2.put("convId", convId);
            m2.put("userId", userB);
            m2.put("role", "MEMBER");
            chatMapper.insertConversationMember(m2);
        }

        // 如果你需要把创建者标记为 OWNER（如确保第一位成员为 OWNER），可在上面分支中将 userA 设置为 OWNER，
        // 这里保持与原有实现一致：针对商品会话的处理已覆盖 OWNER/MEMBER 的分配。
        return c;
    }

    @Override
    @Transactional
    public Long ensureConversation(Long goodsId, Long userA, Long userB) {
        Long existing = findConversationByGoodsAndUsers(goodsId, userA, userB);
        if (existing != null) return existing;
        Conversation conv = createConversation(goodsId, userA, userB);
        return conv.getId();
    }

    @Override
    public ChatMessage sendMessage(Long senderId, Long convId, String type, String content) {
        ChatMessage m = new ChatMessage();
        m.setConversationId(convId);
        m.setSenderId(senderId);
        m.setContent(content);
        m.setType(type);
        m.setStatus("sent");
        m.setCreateTime(LocalDateTime.now());
        chatMapper.insertMessage(m);
        return m;
    }

    @Override
    public List<ChatMessage> getMessages(Long convId, int offset, int limit) {
        Map<String,Object> p = new HashMap<>();
        p.put("convId", convId);
        p.put("offset", offset);
        p.put("limit", limit);
        return chatMapper.selectMessagesByConv(p);
    }

    @Override
    public List<ChatMessage> getNewMessages(Long convId, String since) {
        Map<String,Object> p = new HashMap<>();
        p.put("convId", convId);
        p.put("since", since);
        return chatMapper.selectNewMessages(p);
    }

    @Override
    public int countUnread(Long convId, Long userId) {
        Map<String,Object> p = new HashMap<>();
        p.put("convId", convId);
        p.put("userId", userId);
        return chatMapper.countUnread(p);
    }

    @Override
    public List<Map<String, Object>> listConversations(Long userId, String role) {
        if (userId == null) return Collections.emptyList();

        Map<String,Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("role", role);
        List<Map<String,Object>> list = chatMapper.selectConversationsByUser(params);
        if (list == null) return Collections.emptyList();

        for (Map<String,Object> row : list) {
            if (!row.containsKey("partnerId") || row.get("partnerId") == null) row.put("partnerId", 0L);
            if (!row.containsKey("partnerName") || row.get("partnerName") == null) row.put("partnerName", "");
            if (!row.containsKey("partnerAvatar") || row.get("partnerAvatar") == null) row.put("partnerAvatar", "");
            if (!row.containsKey("lastMessage") || row.get("lastMessage") == null) row.put("lastMessage", "");
            if (!row.containsKey("lastTime") || row.get("lastTime") == null) row.put("lastTime", row.get("create_time"));
            if (!row.containsKey("unread") || row.get("unread") == null) row.put("unread", 0);
            if (!row.containsKey("goodsId")) row.put("goodsId", null);

            // 尝试从返回的数据获取 conversation_member 的角色（优先）
            String partnerMemberRole = null;
            if (row.containsKey("partnerMemberRole") && row.get("partnerMemberRole") != null) {
                partnerMemberRole = String.valueOf(row.get("partnerMemberRole"));
                if (partnerMemberRole != null && partnerMemberRole.trim().isEmpty()) partnerMemberRole = null;
            }

            // goodsSellerId 解析
            Object gsObj = row.get("goodsSellerId");
            Long goodsSellerId = null;
            if (gsObj instanceof Number) goodsSellerId = ((Number) gsObj).longValue();
            else if (gsObj != null) {
                try { goodsSellerId = Long.parseLong(String.valueOf(gsObj)); } catch (Exception ignored) {}
            }

            // 决定我方角色：优先使用 conversation_member.role（如果为 SELLER/BUYER/MEMBER 等），
            // 若无明确 role，再使用 goodsSellerId 与当前 userId 比较来推断。
            String myRole = null;
            if (partnerMemberRole != null) {
                // partnerMemberRole 表示对方在该会话中的 role（cm_partner.role），
                // 因此如果 partnerMemberRole == "SELLER" 则我方为 BUYER，反之亦然。
                String pr = partnerMemberRole.trim();
                if ("SELLER".equalsIgnoreCase(pr)) myRole = "BUYER";
                else if ("BUYER".equalsIgnoreCase(pr)) myRole = "SELLER";
                else if ("MEMBER".equalsIgnoreCase(pr)) {
                    // 无法从 partnerMemberRole 推断谁是卖家，回退到 goodsSellerId
                    myRole = null;
                }
            }

            if (myRole == null) {
                boolean iAmSeller = goodsSellerId != null && Objects.equals(goodsSellerId, userId);
                myRole = iAmSeller ? "SELLER" : "BUYER";
            }

            String partnerRole = "SELLER".equalsIgnoreCase(myRole) ? "BUYER" : "SELLER";

            row.put("myRole", myRole);
            row.put("partnerRole", partnerRole);
        }

        // 如果前端传 role 参数（"buyer" 或 "seller"），在 service 层做二次过滤（防止前端越权）
        if (role != null) {
            String r = role.trim().toLowerCase();
            Iterator<Map<String,Object>> it = list.iterator();
            while (it.hasNext()) {
                Map<String,Object> row = it.next();
                String myRole = (String) row.get("myRole");
                if ("seller".equals(r) && !"SELLER".equalsIgnoreCase(myRole)) it.remove();
                if ("buyer".equals(r) && !"BUYER".equalsIgnoreCase(myRole)) it.remove();
            }
        }

        return list;
    }

    @Override
    public Conversation getConversation(Long convId) {
        return chatMapper.selectConversationById(convId);
    }

    // 额外工具方法：获取 member 角色
    public String getMemberRole(Long convId, Long userId) {
        Map<String,Object> p = new HashMap<>();
        p.put("convId", convId);
        p.put("userId", userId);
        return chatMapper.selectMemberRole(p);
    }

    /**
     * 新增方法：返回会话详情（包含 myRole / partnerRole / partnerId / partnerName / goodsId）
     * 返回 null 表示会话不存在或当前 userId 非会话成员（无权限）
     */
    @Override
    public Map<String, Object> getConversationDetail(Long convId, Long userId) {
        if (convId == null || userId == null) return null;

        // 检查会话是否存在及当前用户是否为成员（通过 conversation_member）
        Map<String,Object> checkParam = new HashMap<>();
        checkParam.put("convId", convId);
        checkParam.put("userId", userId);
        String myRoleFromMember = chatMapper.selectMemberRole(checkParam);
        // 如果 selectMemberRole 返回 null，说明当前用户不是该会话成员 -> 无权限
        if (myRoleFromMember == null) {
            // 但为了兼容部分历史数据，仍检查 conversation 是否存在 and maybe public?
            Conversation conv = getConversation(convId);
            if (conv == null) return null;
            // 如果用户不是成员则视为无权访问
            return null;
        }

        Conversation conv = getConversation(convId);
        if (conv == null) return null;

        Map<String,Object> res = new HashMap<>();
        res.put("convId", convId);
        res.put("goodsId", conv.getGoodsId() == null ? 0L : conv.getGoodsId());

        // 获取会话中的另一方（partner）
        Map<String,Object> partnerParam = new HashMap<>();
        partnerParam.put("convId", convId);
        partnerParam.put("userId", userId);
        Map<String,Object> partner = chatMapper.selectConversationPartner(partnerParam);

        Long partnerId = null;
        String partnerRole = null;
        String partnerName = null;
        if (partner != null) {
            Object pid = partner.get("userId");
            if (pid instanceof Number) partnerId = ((Number) pid).longValue();
            else if (pid != null) {
                try { partnerId = Long.parseLong(String.valueOf(pid)); } catch (Exception ignored) {}
            }
            partnerRole = (String) partner.get("role");
            partnerName = (String) partner.get("name");
        }

        String myRole = myRoleFromMember;
        // 如果 member 表中没有明确角色（理论上不会，因为 selectMemberRole 已返回），则回退到 goods seller 推断
        if (myRole == null) {
            Long goodsSellerId = null;
            if (conv.getGoodsId() != null) {
                goodsSellerId = chatMapper.selectGoodsSellerId(conv.getGoodsId());
            }
            boolean iAmSeller = goodsSellerId != null && Objects.equals(goodsSellerId, userId);
            myRole = iAmSeller ? "SELLER" : "BUYER";
            partnerRole = iAmSeller ? "BUYER" : "SELLER";
        } else {
            if (partnerRole == null && myRole != null) {
                partnerRole = "SELLER".equalsIgnoreCase(myRole) ? "BUYER" : "SELLER";
            }
        }

        res.put("myRole", myRole);
        res.put("partnerRole", partnerRole == null ? "" : partnerRole);
        res.put("partnerId", partnerId == null ? 0L : partnerId);
        res.put("partnerName", partnerName == null ? "" : partnerName);
        return res;
    }
}