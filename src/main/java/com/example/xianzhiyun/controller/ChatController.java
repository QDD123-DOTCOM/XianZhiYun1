package com.example.xianzhiyun.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.example.xianzhiyun.entity.ChatMessage;
import com.example.xianzhiyun.entity.Conversation;
import com.example.xianzhiyun.service.ChatService;
import com.example.xianzhiyun.utils.JsonResult;

import java.util.*;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @PostMapping("/ensure")
    public JsonResult<Map<String, Object>> ensure(@RequestBody Map<String, Object> body,
                                                  @RequestAttribute("userId") Long userId) {
        Long goodsId = body.get("goodsId")==null ? null : Long.parseLong(String.valueOf(body.get("goodsId")));
        Long otherUserId = body.get("otherUserId")==null ? null : Long.parseLong(String.valueOf(body.get("otherUserId")));
        if (otherUserId == null) return JsonResult.error(400, "otherUserId 必需");

        Long convId = chatService.ensureConversation(goodsId, userId, otherUserId);

        String myRole = null;
        try {
            Map<String,Object> detail = chatService.getConversationDetail(convId, userId);
            if (detail != null) myRole = (String) detail.get("myRole");
        } catch (Exception ignored) {}

        Map<String,Object> res = new HashMap<>();
        res.put("convId", convId);
        res.put("myRole", myRole);
        return JsonResult.success(res);
    }

    @PostMapping("/send")
    public JsonResult<ChatMessage> send(@RequestBody Map<String, Object> body,
                                        @RequestAttribute("userId") Long userId) {
        Long convId = body.get("convId")==null ? null : Long.parseLong(String.valueOf(body.get("convId")));
        String type = body.get("type")==null ? "text" : String.valueOf(body.get("type"));
        String content = body.get("content")==null ? null : String.valueOf(body.get("content"));

        ChatMessage m = chatService.sendMessage(userId, convId, type, content);
        return JsonResult.success(m);
    }

    @GetMapping("/conv/{convId}")
    public JsonResult<Map<String,Object>> conv(@PathVariable Long convId,
                                               @RequestAttribute("userId") Long userId) {
        Conversation conv = chatService.getConversation(convId);
        if (conv == null) return JsonResult.error(404, "会话不存在");

        Map<String,Object> convDetail = chatService.getConversationDetail(convId, userId);
        if (convDetail == null) return JsonResult.error(403, "无权访问该会话");

        List<ChatMessage> msgs = chatService.getMessages(convId, 0, 200);
        if (msgs == null) msgs = Collections.emptyList();

        Map<String,Object> result = new HashMap<>();
        result.put("convId", convId);
        result.put("myRole", convDetail.get("myRole"));
        result.put("partnerRole", convDetail.get("partnerRole"));
        result.put("partnerName", convDetail.get("partnerName") == null ? "" : convDetail.get("partnerName"));
        result.put("messages", msgs);
        result.put("goodsId", convDetail.get("goodsId") == null ? 0L : convDetail.get("goodsId"));
        return JsonResult.success(result);
    }

    @GetMapping("/conv/{convId}/new")
    public JsonResult<Map<String,Object>> newMsgs(@PathVariable Long convId,
                                                  @RequestParam(required=false) String since,
                                                  @RequestAttribute("userId") Long userId) {
        if (since == null || since.trim().isEmpty()) {
            return JsonResult.success(Map.of("items", Collections.emptyList()));
        }
        Map<String,Object> convDetail = chatService.getConversationDetail(convId, userId);
        if (convDetail == null) return JsonResult.error(403, "无权访问该会话");

        List<ChatMessage> items = chatService.getNewMessages(convId, since);
        if (items == null) items = Collections.emptyList();
        return JsonResult.success(Map.of("items", items));
    }

    @GetMapping("/list")
    public JsonResult<List<Map<String,Object>>> list(@RequestParam(required=false) String role,
                                                     @RequestAttribute("userId") Long userId) {
        List<Map<String,Object>> list = chatService.listConversations(userId, role);
        return JsonResult.success(list);
    }
}