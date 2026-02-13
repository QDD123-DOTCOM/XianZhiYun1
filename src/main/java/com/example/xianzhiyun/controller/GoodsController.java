package com.example.xianzhiyun.controller;

import com.example.xianzhiyun.dto.GoodsPublishDTO;
import com.example.xianzhiyun.entity.Comment;
import com.example.xianzhiyun.entity.GoodsItem;
import com.example.xianzhiyun.entity.UserInfo;
import com.example.xianzhiyun.service.CommentService;
import com.example.xianzhiyun.service.FavoriteService;
import com.example.xianzhiyun.service.GoodsService;
import com.example.xianzhiyun.service.UserInfoService;
import com.example.xianzhiyun.utils.JsonResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class GoodsController {

    private static final Logger logger = LoggerFactory.getLogger(GoodsController.class);

    @Autowired
    private GoodsService goodsService;
    @Autowired
    private UserInfoService userInfoService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private FavoriteService favoriteService;

    // ---------------------- 商品列表接口 (公开) ----------------------
    @GetMapping("/goods")
    public JsonResult<Map<String, Object>> list(@RequestParam(value = "categoryKey", required = false) String categoryKey,
                                                @RequestParam(value = "keyword", required = false) String keyword,
                                                @RequestParam(value = "filterType", required = false) String filterType,
                                                @RequestParam(value = "page", defaultValue = "1") int page,
                                                @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        if (page < 1) page = 1;
        if (pageSize <= 0) pageSize = 10;
        List<GoodsItem> items = goodsService.listGoods(categoryKey, keyword, filterType, page, pageSize);
        int total = goodsService.countGoods(categoryKey, keyword, filterType);
        List<Map<String, Object>> outItems = items.stream().map(this::convertToMap).collect(Collectors.toList());
        Map<String, Object> resp = new HashMap<>();
        resp.put("items", outItems);
        resp.put("total", total);
        resp.put("page", page);
        resp.put("pageSize", pageSize);
        return JsonResult.success(resp);
    }

    // ---------------------- 商品详情接口 (半公开) ----------------------
    @GetMapping(value = "/goods/{id:\\d+}")
    public JsonResult<Map<String, Object>> get(@PathVariable Long id,
                                               @RequestAttribute(value = "userId", required = false) Long currentUserId) {
        GoodsItem item = goodsService.getById(id);
        if (item == null) return JsonResult.error(404, "商品未找到");
        Map<String, Object> respData = convertToMap(item);
        Long sellerId = item.getSellerId();
        if (sellerId != null) {
            UserInfo seller = userInfoService.getById(sellerId);
            if (seller != null) {
                Map<String, Object> s = new HashMap<>();
                s.put("id", seller.getId());
                s.put("nickname", seller.getNickname());
                s.put("avatarUrl", seller.getAvatarUrl());
                s.put("creditScore", seller.getCreditScore());
                respData.put("seller", s);
            }
        }
        List<Comment> comments = commentService.getCommentsByGoodsId(id, 0, 10);
        List<Map<String,Object>> commentOut = comments.stream().map(c -> {
            Map<String,Object> m = new HashMap<>();
            m.put("id", c.getId());
            m.put("content", c.getContent());
            m.put("createTime", c.getCreateTime());
            UserInfo u = userInfoService.getById(c.getUserId());
            if (u != null) {
                m.put("nickname", u.getNickname());
                m.put("avatarUrl", u.getAvatarUrl());
            }
            return m;
        }).collect(Collectors.toList());
        respData.put("comments", commentOut);

        // 补充收藏状态
        if (currentUserId != null) {
            boolean fav = favoriteService.isFavorited(currentUserId, id);
            respData.put("favorited", fav);
        } else {
            respData.put("favorited", false);
        }

        return JsonResult.success(respData);
    }

    // ---------------------- 卖家其他商品 (公开) ----------------------
    @GetMapping("/goods/bySeller")
    public JsonResult<Map<String, Object>> listBySeller(@RequestParam Long sellerId,
                                                        @RequestParam(required = false) Long excludeId,
                                                        @RequestParam(defaultValue = "1") int page,
                                                        @RequestParam(defaultValue = "10") int pageSize) {
        if (page < 1) page = 1;
        int offset = (page - 1) * pageSize;
        List<GoodsItem> items = goodsService.getGoodsBySeller(sellerId, offset, pageSize);
        int total = goodsService.countBySellerId(sellerId);

        if (excludeId != null && excludeId > 0) {
            items = items.stream().filter(g -> !Objects.equals(g.getId(), excludeId)).collect(Collectors.toList());
        }
        List<Map<String, Object>> out = items.stream().map(this::convertToMap).collect(Collectors.toList());

        Map<String, Object> resp = new HashMap<>();
        resp.put("items", out);
        resp.put("total", total);
        return JsonResult.success(resp);
    }

    // ---------------------- 需要登录的操作 ----------------------

    @PostMapping("/goods")
    public JsonResult<Map<String, Long>> publish(@Validated @RequestBody GoodsPublishDTO dto,
                                                 @RequestAttribute("userId") Long sellerId) {
        Long id = goodsService.publish(sellerId, dto);
        return JsonResult.success(Map.of("id", id));
    }

    @PutMapping("/goods/{id:\\d+}")
    public JsonResult<Void> update(@PathVariable Long id,
                                   @Validated @RequestBody GoodsPublishDTO dto,
                                   @RequestAttribute("userId") Long sellerId) {
        GoodsItem old = goodsService.getById(id);
        if (old == null) return JsonResult.error(404, "商品不存在");
        if (!Objects.equals(old.getSellerId(), sellerId)) return JsonResult.error(403, "无权限编辑该商品");

        goodsService.update(id, dto);
        return JsonResult.success(null);
    }

    @PutMapping("/goods/{id:\\d+}/delist")
    public JsonResult<Void> delist(@PathVariable Long id, @RequestAttribute("userId") Long sellerId) {
        GoodsItem old = goodsService.getById(id);
        if (old == null) return JsonResult.error(404,"没有该商品");
        if (!Objects.equals(old.getSellerId(), sellerId)) return JsonResult.error(403,"无权利下架该产品");
        try {
            goodsService.delist(id,sellerId);
            return JsonResult.success(null);
        } catch (Exception e){
            logger.error("delist error", e);
            return JsonResult.error(500,"下架商品失败");
        }
    }

    @DeleteMapping("/goods/{id:\\d+}")
    public JsonResult<Void> deleteWithAuth(@PathVariable Long id, @RequestAttribute("userId") Long sellerId) {
        GoodsItem old = goodsService.getById(id);
        if (old == null) return JsonResult.error(404, "商品不存在");
        if (!Objects.equals(old.getSellerId(), sellerId)) return JsonResult.error(403, "无权限删除该商品");

        try {
            goodsService.delete(id, sellerId);
            return JsonResult.success(null);
        } catch (Exception e) {
            logger.error("delete error", e);
            return JsonResult.error(500, "删除商品失败: " + e.getMessage());
        }
    }

    @PostMapping("/goods/{id:\\d+}/resubmit")
    public JsonResult<Void> resubmit(@PathVariable Long id, @RequestAttribute("userId") Long sellerId) {
        GoodsItem old = goodsService.getById(id);
        if (old == null) return JsonResult.error(404, "商品不存在");
        if (!Objects.equals(old.getSellerId(), sellerId)) return JsonResult.error(403, "无权限操作该商品");

        goodsService.resubmit(id);
        return JsonResult.success(null);
    }

    @GetMapping("/user/myGoods")
    public JsonResult<Map<String, Object>> myGoods(@RequestParam(defaultValue = "1") int page,
                                                   @RequestParam(defaultValue = "10") int size,
                                                   @RequestAttribute("userId") Long userId) {
        if (page < 1) page = 1;
        List<GoodsItem> list = goodsService.getMyGoods(userId, page, size);
        int total = goodsService.countMyGoods(userId);
        List<Map<String, Object>> outList = list.stream().map(this::convertToMap).collect(Collectors.toList());

        Map<String, Object> resp = new HashMap<>();
        resp.put("items", outList);
        resp.put("total", total);
        resp.put("page", page);
        resp.put("size", size);
        return JsonResult.success(resp);
    }

    @GetMapping("/goods/batch")
    public JsonResult<List<Map<String, Object>>> batchByIds(@RequestParam(value = "ids", required = false) String idsParam) {
        if (idsParam == null || idsParam.trim().isEmpty()) return JsonResult.success(new ArrayList<>());

        List<Long> ids = Arrays.stream(idsParam.split(","))
                .map(String::trim).filter(s -> !s.isEmpty())
                .map(s -> { try { return Long.parseLong(s); } catch (NumberFormatException e) { return null; } })
                .filter(Objects::nonNull).distinct().collect(Collectors.toList());

        if (ids.isEmpty()) return JsonResult.success(new ArrayList<>());
        Map<Long, GoodsItem> map = goodsService.mapByIds(ids);

        List<Map<String, Object>> out = new ArrayList<>();
        for (Long id : ids) {
            if (map.containsKey(id)) out.add(convertToMap(map.get(id)));
        }
        return JsonResult.success(out);
    }

    /**
     * 辅助方法：统一将 GoodsItem 转换为前端需要的 Map 格式
     * 包含真实的统计数据：浏览量、喜欢数(收藏)、评论数、咨询数
     */
    private Map<String, Object> convertToMap(GoodsItem g) {
        Map<String, Object> m = new HashMap<>();
        m.put("id", g.getId());
        m.put("sellerId", g.getSellerId());
        m.put("title", g.getTitle());
        m.put("description", g.getDescription());
        m.put("price", g.getPrice());
        m.put("category", g.getCategory());
        m.put("itemType", g.getItemType());
        m.put("status", g.getStatus());
        m.put("createTime", g.getCreateTime());
        m.put("updateTime", g.getUpdateTime());

        // --- 核心修改：映射统计字段给前端 ---
        // 浏览量
        m.put("viewCount", g.getViewCount() != null ? g.getViewCount() : 0);
        // 喜欢数（对应数据库中的收藏数 favCount）
        m.put("likeCount", g.getFavCount() != null ? g.getFavCount() : 0);
        // 评论数
        m.put("commentCount", g.getCommentCount() != null ? g.getCommentCount() : 0);
        // 咨询数（根据您的逻辑，咨询即评论，所以映射 commentCount）
        m.put("chatCount", g.getCommentCount() != null ? g.getCommentCount() : 0);

        // 处理图片 URL
        String cvs = g.getCoverUrls();
        List<String> list = new ArrayList<>();
        if (cvs != null && !cvs.isBlank()) {
            list = Arrays.stream(cvs.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());
        }
        m.put("coverUrls", list);
        m.put("coverUrl", list.isEmpty() ? "" : list.get(0));

        return m;
    }
}
