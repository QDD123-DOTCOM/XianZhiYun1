package com.example.xianzhiyun.controller;

import com.example.xianzhiyun.dto.ArticleDTO;
import com.example.xianzhiyun.dto.CommunityDTO;
import com.example.xianzhiyun.dto.CommunityDetailDTO;
import com.example.xianzhiyun.dto.CommunityRecommendDTO;
import com.example.xianzhiyun.entity.CommunityArticleComment;
import com.example.xianzhiyun.entity.CommunityCategory;
import com.example.xianzhiyun.service.CommunityCategoryService;
import com.example.xianzhiyun.service.CommunityService;
import com.example.xianzhiyun.utils.JsonResult;
import com.example.xianzhiyun.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest; // 【修改点1】使用 jakarta 包
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class CommunityController {

    @Autowired
    private CommunityService communityService;

    @Autowired
    private CommunityCategoryService categoryService;

    @Autowired
    private JwtUtil jwtUtil; // 【修改点2】注入 JwtUtil 实例，而不是静态调用

    // ================= 社区相关 =================

    @GetMapping("/communities")
    public JsonResult list(@RequestParam(value = "category", required = false) String category,
                           @RequestParam(value = "page", defaultValue = "1") int page,
                           @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        List<CommunityDTO> items = communityService.listCommunities(category, page, pageSize);
        int total = communityService.countCommunities(category);
        Map<String, Object> data = new HashMap<>();
        data.put("items", items);
        data.put("total", total);
        return JsonResult.ok(data);
    }

    @GetMapping("/community/detail/{id}")
    public JsonResult detail(@PathVariable("id") Long id,
                             @RequestAttribute(value = "userId", required = false) Long userId) {
        CommunityDetailDTO dto = communityService.getCommunityDetail(id, userId);
        if (dto == null) return JsonResult.fail("社区不存在");
        return JsonResult.ok(dto);
    }

    @GetMapping("/community/topics")
    public JsonResult topics(@RequestParam Long communityId,
                             @RequestParam(defaultValue = "latest") String type,
                             @RequestParam(defaultValue = "1") int page,
                             @RequestParam(defaultValue = "10") int pageSize,
                             @RequestAttribute(value = "userId", required = false) Long userId) {
        List<ArticleDTO> list = communityService.getTopics(communityId, type, page, pageSize, userId);
        return JsonResult.ok(list);
    }

    @PostMapping("/community/join")
    public JsonResult join(@RequestBody Map<String, Object> body,
                           @RequestAttribute("userId") Long userId) {
        Object cidObj = body.get("communityId");
        Object actionObj = body.get("action");
        if (cidObj == null) return JsonResult.fail("参数缺失");

        Long communityId = Long.valueOf(String.valueOf(cidObj));
        String action = actionObj == null ? "join" : String.valueOf(actionObj);

        boolean ok;
        if ("leave".equalsIgnoreCase(action)) {
            ok = communityService.leaveCommunity(userId, communityId);
        } else {
            ok = communityService.joinCommunity(userId, communityId);
        }
        return ok ? JsonResult.ok("操作成功") : JsonResult.fail("操作失败");
    }

    @GetMapping("/community/categories")
    public JsonResult categories() {
        List<CommunityCategory> list = categoryService.listActiveCategories();
        return JsonResult.ok(list);
    }

    // ================= 帖子(Article)相关 =================

    @GetMapping("/post/detail/{id}")
    public JsonResult postDetail(@PathVariable("id") Long id,
                                 @RequestAttribute(value = "userId", required = false) Long userId) {
        ArticleDTO dto = communityService.getArticleDetail(id, userId);
        if (dto == null) return JsonResult.fail("帖子不存在");
        return JsonResult.ok(dto);
    }

    @PostMapping("/post/publish")
    public JsonResult publishPost(@RequestBody ArticleDTO postDto,
                                  @RequestAttribute("userId") Long userId) {
        if (postDto.getCommunityId() == null || postDto.getContent() == null) {
            return JsonResult.fail("参数不完整");
        }
        postDto.setAuthorId(userId);
        Long articleId = communityService.publishArticle(postDto);
        return JsonResult.ok(Map.of("articleId", articleId));
    }

    @PostMapping("/post/like")
    public JsonResult likePost(@RequestBody Map<String, Long> body,
                               @RequestAttribute("userId") Long userId) {
        Long articleId = body.get("articleId");
        if (articleId == null) return JsonResult.fail("参数缺失");

        boolean isLiked = communityService.toggleLikeArticle(userId, articleId);
        return JsonResult.ok(isLiked ? "Liked" : "Unliked");
    }

    // ================= 评论相关 =================

    @GetMapping("/post/comments")
    public JsonResult<List<CommunityArticleComment>> getComments(@RequestParam Long articleId) {
        List<CommunityArticleComment> list = communityService.getArticleComments(articleId);
        return JsonResult.ok(list);
    }

    @PostMapping("/post/comment/add")
    public JsonResult<String> addComment(@RequestBody Map<String, Object> body,
                                         @RequestAttribute("userId") Long userId) {
        Object articleIdObj = body.get("articleId");
        String content = (String) body.get("content");

        if (articleIdObj == null || content == null || content.trim().isEmpty()) {
            return JsonResult.fail("参数错误：缺少文章ID或内容");
        }
        Long articleId = Long.valueOf(String.valueOf(articleIdObj));
        communityService.addArticleComment(userId, articleId, content);
        return JsonResult.ok("评论成功");
    }

    // ================= 新增：商品推荐社区接口 =================
    /**
     * 获取商品详情页推荐社区
     */
    @GetMapping("/community/recommend")
    public JsonResult<CommunityRecommendDTO> getRecommendedCommunity(@RequestParam Long goodsId, HttpServletRequest request) {
        Long userId = null;
        try {
            // 【修改点3】直接使用注入的 jwtUtil 实例的方法，更加简洁安全
            userId = jwtUtil.getUserIdFromRequest(request);
        } catch (Exception e) {
            System.err.println("Failed to get userId from token: " + e.getMessage());
        }

        CommunityRecommendDTO community = communityService.getRecommendedCommunityByGoodsId(goodsId, userId);
        if (community != null) {
            return JsonResult.ok(community);
        } else {
            return JsonResult.fail("未找到推荐社区，或服务异常");
        }
    }
}