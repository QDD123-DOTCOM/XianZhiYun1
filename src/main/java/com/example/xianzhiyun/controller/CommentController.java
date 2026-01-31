package com.example.xianzhiyun.controller;

import com.example.xianzhiyun.entity.Comment;
import com.example.xianzhiyun.service.CommentService;
import com.example.xianzhiyun.utils.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @GetMapping("/goods/{goodsId}/comments")
    public JsonResult<Map<String, Object>> list(@PathVariable Long goodsId,
                                                @RequestParam(defaultValue = "1") int page,
                                                @RequestParam(defaultValue = "10") int size) {
        int offset = (Math.max(page,1)-1) * size;
        List<Comment> items = commentService.getCommentsByGoodsId(goodsId, offset, size);
        int total = commentService.countByGoodsId(goodsId);
        return JsonResult.success(Map.of("items", items, "total", total));
    }

    @PostMapping("/goods/{goodsId}/comments")
    public JsonResult<Void> add(@PathVariable Long goodsId,
                                @RequestBody Map<String, Object> body,
                                @RequestAttribute("userId") Long userId) {
        String content = (String) body.get("content");
        if (content == null || content.trim().isEmpty()) {
            return JsonResult.error(400, "内容为空");
        }

        Byte rating = null;
        if (body.containsKey("rating")) {
            try {
                rating = Byte.valueOf(body.get("rating").toString());
            } catch (NumberFormatException e) {
                rating = 5;
            }
        }

        commentService.addComment(goodsId, userId, content.trim(), rating);
        return JsonResult.success(null);
    }
}