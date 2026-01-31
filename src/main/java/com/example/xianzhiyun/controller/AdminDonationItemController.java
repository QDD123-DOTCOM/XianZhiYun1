package com.example.xianzhiyun.controller;

import com.example.xianzhiyun.entity.DonationItem;
import com.example.xianzhiyun.entity.DonationReview;
import com.example.xianzhiyun.service.DonationService;
import com.example.xianzhiyun.service.ReviewService;
import com.example.xianzhiyun.utils.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理端：活动下商品审核接口
 */
@RestController
@RequestMapping("/api/admin")
public class AdminDonationItemController {

    @Autowired
    private DonationService donationService; // 你已有的 DonationServiceImpl 实现 selectList/selectCount 等

    @Autowired(required = false)
    private ReviewService reviewService; // 你已有 ReviewServiceImpl

    /*
    // 注释掉以避免与 AdminDonationEventController 的 /events/{id}/items 冲突
    @GetMapping("/events/{eventId:\\d+}/items")
    public JsonResult<Map<String, Object>> listByEvent(@PathVariable Long eventId,
                                                       @RequestParam(value = "status", required = false) String status,
                                                       @RequestParam(value = "page", defaultValue = "1") int page,
                                                       @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                                       @RequestParam(value = "keyword", required = false) String keyword) {
        if (page < 1) page = 1;
        if (pageSize <= 0) pageSize = 10;
        int offset = (page - 1) * pageSize;

        Map<String, Object> params = new HashMap<>();
        Map<String, Object> inner = new HashMap<>();
        inner.put("eventId", eventId);
        inner.put("status", status);
        inner.put("keyword", keyword);
        inner.put("offset", offset);
        inner.put("limit", pageSize);
        params.put("params", inner); // your mapper expects params.* based on DonationItemMapper.xml

        List<DonationItem> items = donationService.list(params);
        int total = donationService.count(params);

        Map<String, Object> resp = new HashMap<>();
        resp.put("items", items);
        resp.put("total", total);
        resp.put("page", page);
        resp.put("pageSize", pageSize);
        return JsonResult.success(resp);
    }
    */

    /**
     * POST /api/admin/items/{itemId}/review
     * 审核单个捐赠物品（APPROVED/REJECTED）
     * 请求体 JSON: { "action": "APPROVED" | "REJECTED", "comment": "..." }
     */
    @PostMapping("/items/{itemId:\\d+}/review")
    public JsonResult<Void> reviewItem(@PathVariable Long itemId, @RequestBody Map<String, String> body) {
        if (reviewService == null) return JsonResult.error(501, "审核服务未实现");

        String action = body.get("action");
        String comment = body.get("comment");

        // 1) 创建审核记录
        DonationReview review = new DonationReview();
        review.setItemId(itemId);
        review.setReviewStatus(action);
        review.setReviewComment(comment);
        // reviewerId 可从当前 admin token 中解析并设置（此处留空或由 service 填充）
        reviewService.create(review);

        // 2) 根据审核结果更新 item 状态
        if ("APPROVED".equalsIgnoreCase(action) || "APPROVE".equalsIgnoreCase(action)) {
            // 通过后上架（或者你希望是 ON_SHELF）
            Map<String, Object> updateParams = new HashMap<>();
            updateParams.put("id", itemId);
            updateParams.put("status", "ON_SHELF");
            donationService.updateStatus(updateParams);
        } else {
            Map<String, Object> updateParams = new HashMap<>();
            updateParams.put("id", itemId);
            updateParams.put("status", "REJECTED");
            donationService.updateStatus(updateParams);
        }

        return JsonResult.success(null);
    }
}