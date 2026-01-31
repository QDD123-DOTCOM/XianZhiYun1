package com.example.xianzhiyun.controller;

import com.example.xianzhiyun.entity.GoodsItem;
import com.example.xianzhiyun.service.GoodsService;
import com.example.xianzhiyun.utils.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理端商品（Goods）管理接口
 */
@RestController
@RequestMapping("/api/admin/goods")
public class AdminGoodsController {

    @Autowired
    private GoodsService goodsService;

    @GetMapping
    public JsonResult<Map<String, Object>> list(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "sellerId", required = false) Long sellerId,
            @RequestParam(value = "sellerKeyword", required = false) String sellerKeyword,
            @RequestParam(value = "category", required = false) String category) {

        int offset = Math.max(page, 1) - 1;

        Map<String, Object> params = new HashMap<>();
        params.put("offset", offset);
        params.put("limit", pageSize);
        if (keyword != null && !keyword.trim().isEmpty()) params.put("keyword", keyword.trim());
        if (status != null && !status.trim().isEmpty()) params.put("status", status.trim());
        if (sellerId != null) params.put("sellerId", sellerId);
        if (sellerKeyword != null && !sellerKeyword.trim().isEmpty()) params.put("sellerKeyword", sellerKeyword.trim());
        if (category != null && !category.trim().isEmpty()) params.put("category", category.trim());

        List<GoodsItem> items = goodsService.listGoodsForAdmin(params);
        int total = goodsService.countGoodsForAdmin(params);

        Map<String, Object> resp = new HashMap<>();
        resp.put("items", items);
        resp.put("total", total);
        resp.put("page", page);
        resp.put("pageSize", pageSize);
        return JsonResult.success(resp);
    }

    @GetMapping("/{id:\\d+}")
    public JsonResult<GoodsItem> get(@PathVariable Long id) {
        GoodsItem g = goodsService.getById(id);
        if (g == null) return JsonResult.error(404, "商品不存在");
        return JsonResult.success(g);
    }

    /**
     * POST /api/admin/goods/{id}/status
     * body: { "status": "ON_SALE" }
     */
    @PostMapping("/{id:\\d+}/status")
    public JsonResult<Void> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String status = body.get("status");
        if (status == null || status.trim().isEmpty()) {
            return JsonResult.error(400, "status required");
        }
        status = status.trim();

        // 与数据库枚举保持一致
        final java.util.Set<String> ALLOWED =
                java.util.Set.of("DRAFT", "PENDING", "ON_SALE", "SOLD", "REJECTED", "DELETED");

        if (!ALLOWED.contains(status)) {
            return JsonResult.error(400, "invalid status value: " + status);
        }

        try {
            goodsService.updateStatus(id, status);
            return JsonResult.success(null);
        } catch (IllegalArgumentException e) {
            return JsonResult.error(400, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.error(500, "服务器内部错误");
        }
    }

    @DeleteMapping("/{id:\\d+}")
    public JsonResult<Void> delete(@PathVariable Long id) {
        goodsService.delete(id);
        return JsonResult.success(null);
    }
}