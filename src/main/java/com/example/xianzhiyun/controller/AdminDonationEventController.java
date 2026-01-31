package com.example.xianzhiyun.controller;

import com.example.xianzhiyun.entity.DonationEvent;
import com.example.xianzhiyun.entity.DonationFeedback;
import com.example.xianzhiyun.entity.DonationItem;
import com.example.xianzhiyun.entity.DonationReview;
import com.example.xianzhiyun.service.DonationService;
import com.example.xianzhiyun.service.EventService;
import com.example.xianzhiyun.service.ReviewService;
import com.example.xianzhiyun.service.DonationFeedbackService;
import com.example.xianzhiyun.utils.JsonResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理端活动接口（Admin）
 */
@RestController
@RequestMapping("/api/admin")
public class AdminDonationEventController {

    private static final Logger logger = LoggerFactory.getLogger(AdminDonationEventController.class);

    @Autowired
    private EventService eventService;

    @Autowired(required = false)
    private ReviewService reviewService;

    @Autowired(required = false)
    private DonationFeedbackService feedbackService;

    // 注入 DonationService 用于获取活动下的捐赠物品
    @Autowired(required = false)
    private DonationService donationService;

    /**
     * GET /api/admin/events
     * 分页查询活动，支持 keyword/status
     */
    @GetMapping("/events")
    public JsonResult<Map<String, Object>> list(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {

        int offset = Math.max(page, 1) - 1;
        Map<String, Object> params = new HashMap<>();
        params.put("keyword", keyword);
        params.put("status", status);
        params.put("offset", offset);
        params.put("limit", pageSize);

        List<DonationEvent> list = eventService.listAdmin(params);
        int total = eventService.countAdmin(params);

        Map<String, Object> resp = new HashMap<>();
        resp.put("items", list);
        resp.put("total", total);
        resp.put("page", page);
        resp.put("pageSize", pageSize);
        return JsonResult.success(resp);
    }

    /**
     * GET /api/admin/events/{id}
     */
    @GetMapping("/events/{id:\\d+}")
    public JsonResult<DonationEvent> get(@PathVariable Long id) {
        DonationEvent e = eventService.getById(id);
        if (e == null) return JsonResult.error(404, "活动不存在");
        return JsonResult.success(e);
    }

    /**
     * POST /api/admin/events
     */
    @PostMapping("/events")
    public JsonResult<Map<String, Long>> create(@RequestBody DonationEvent dto) {
        Long id = eventService.create(dto);
        Map<String, Long> data = new HashMap<>();
        data.put("id", id);
        return JsonResult.success(data);
    }

    /**
     * PUT /api/admin/events/{id}
     */
    @PutMapping("/events/{id:\\d+}")
    public JsonResult<Void> update(@PathVariable Long id, @RequestBody DonationEvent dto) {
        dto.setId(id);
        eventService.update(dto);
        return JsonResult.success(null);
    }

    /**
     * DELETE /api/admin/events/{id}
     */
    @DeleteMapping("/events/{id:\\d+}")
    public JsonResult<Void> delete(@PathVariable Long id) {
        eventService.delete(id);
        return JsonResult.success(null);
    }

    /**
     * GET /api/admin/events/pending - 待审核活动列表
     */
    @GetMapping("/events/pending")
    public JsonResult<Map<String, Object>> listPending(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {

        int offset = Math.max(page, 1) - 1;
        Map<String, Object> params = new HashMap<>();
        params.put("keyword", keyword);
        params.put("offset", offset);
        params.put("limit", pageSize);

        List<DonationEvent> list = eventService.listPending(params);
        int total = eventService.countPending(params);

        Map<String, Object> resp = new HashMap<>();
        resp.put("items", list);
        resp.put("total", total);
        resp.put("page", page);
        resp.put("pageSize", pageSize);
        return JsonResult.success(resp);
    }

    /**
     * GET /api/admin/events/{id}/items
     * 管理端：获取某活动下的捐赠物品（支持分页、关键字、状态）
     */
    @GetMapping("/events/{id:\\d+}/items")
    public JsonResult<Map<String, Object>> listEventItems(
            @PathVariable Long id,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {

        if (donationService == null) {
            // DonationService 未注入或不可用
            return JsonResult.error(501, "Donation service not available");
        }

        int offset = Math.max(page, 1) - 1;

        Map<String, Object> params = new HashMap<>();
        params.put("eventId", id);
        params.put("status", status);
        params.put("keyword", keyword);
        params.put("offset", offset);
        params.put("limit", pageSize);

        logger.debug("Admin listEventItems params: {}", params);

        List<DonationItem> list = donationService.list(params);
        int total = donationService.count(params);

        Map<String, Object> resp = new HashMap<>();
        resp.put("items", list);
        resp.put("total", total);
        resp.put("page", page);
        resp.put("pageSize", pageSize);
        return JsonResult.success(resp);
    }

    /**
     * POST /api/admin/events/{id}/review
     * body: { "action":"APPROVED"|"REJECTED", "comment":"..." }
     */
    @PostMapping("/events/{id:\\d+}/review")
    public JsonResult<Void> review(@PathVariable Long id, @RequestBody Map<String, String> body) {
        if (reviewService == null) return JsonResult.error(501, "审核服务未实现");
        String action = body.get("action");
        String comment = body.get("comment");
        DonationReview r = new DonationReview();
        r.setItemId(id);
        r.setReviewStatus(action);
        r.setReviewComment(comment);
        reviewService.create(r);
        eventService.applyReviewResult(id, action, comment);
        return JsonResult.success(null);
    }

    /**
     * GET /api/admin/events/{id}/stats
     */
    @GetMapping("/events/{id:\\d+}/stats")
    public JsonResult<Map<String, Object>> stats(@PathVariable Long id) {
        Map<String, Object> stats = new HashMap<>();
        // 使用 eventService 提供的统计（基于 donation_item 等）
        long cnt = eventService.countParticipantsByEventId(id);
        stats.put("participantCount", cnt);
        Map<String, Object> exch = eventService.countExchangeByEventId(id);
        if (exch != null) {
            stats.putAll(exch);
        } else {
            stats.put("exchangeCount", 0);
        }
        return JsonResult.success(stats);
    }

    /**
     * GET /api/admin/events/{id}/feedbacks
     */
    @GetMapping("/events/{id:\\d+}/feedbacks")
    public JsonResult<Map<String, Object>> feedbacks(@PathVariable Long id,
                                                     @RequestParam(value = "limit", required = false) Integer limit) {
        if (feedbackService == null) return JsonResult.error(501, "反馈服务未实现");
        List<DonationFeedback> items = feedbackService.listByEventId(id, limit);
        Map<String, Object> resp = new HashMap<>();
        resp.put("items", items);
        return JsonResult.success(resp);
    }
}