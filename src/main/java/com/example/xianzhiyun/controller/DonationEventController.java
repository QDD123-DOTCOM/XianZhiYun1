package com.example.xianzhiyun.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.example.xianzhiyun.entity.DonationEvent;
import com.example.xianzhiyun.service.EventService;
import com.example.xianzhiyun.utils.JsonResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class DonationEventController {

    @Autowired
    private EventService eventService;

    @GetMapping("/donation_event/{id:\\d+}")
    public JsonResult<DonationEvent> get(@PathVariable Long id) {
        DonationEvent e = eventService.getById(id);
        if (e == null) return JsonResult.error(404, "活动不存在");
        return JsonResult.success(e);
    }

    @GetMapping("/donation_event")
    public JsonResult<Map<String, Object>> list(@RequestParam(value = "keyword", required = false) String keyword,
                                                @RequestParam(value = "page", defaultValue = "1") int page,
                                                @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        int offset = (Math.max(page, 1) - 1) * pageSize;
        Map<String, Object> params = new HashMap<>();
        params.put("keyword", keyword);
        params.put("offset", offset);
        params.put("limit", pageSize);
        List<DonationEvent> list = eventService.list(params);
        Map<String, Object> resp = new HashMap<>();
        resp.put("items", list);
        resp.put("page", page);
        resp.put("pageSize", pageSize);
        return JsonResult.success(resp);
    }

    @PostMapping("/donation_event")
    public JsonResult<Map<String, Long>> create(@RequestBody DonationEvent dto) {
        Long id = eventService.create(dto);
        Map<String, Long> data = new HashMap<>();
        data.put("id", id);
        return JsonResult.success(data);
    }

    @PutMapping("/donation_event/{id:\\d+}")
    public JsonResult<Void> update(@PathVariable Long id, @RequestBody DonationEvent dto) {
        dto.setId(id);
        eventService.update(dto);
        return JsonResult.success(null);
    }

    @DeleteMapping("/donation_event/{id:\\d+}")
    public JsonResult<Void> delete(@PathVariable Long id) {
        eventService.delete(id);
        return JsonResult.success(null);
    }

    // 管理端活动列表（基于 donation_event）
    @GetMapping("/admin/activities")
    public JsonResult<Map<String, Object>> listAdminActivities(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "status", required = false) String status) {

        int offset = Math.max(page, 1) - 1;
        Map<String, Object> params = new HashMap<>();
        params.put("offset", offset * pageSize);
        params.put("limit", pageSize);
        if (keyword != null && !keyword.trim().isEmpty()) params.put("keyword", keyword.trim());
        if (status != null && !status.trim().isEmpty()) params.put("status", status.trim());

        List<DonationEvent> list = eventService.listAdmin(params);
        int total = eventService.countAdmin(params);

        Map<String, Object> resp = new HashMap<>();
        resp.put("items", list);
        resp.put("total", total);
        resp.put("page", page);
        resp.put("pageSize", pageSize);
        return JsonResult.success(resp);
    }

    @GetMapping("/admin/activities/{id:\\d+}")
    public JsonResult<DonationEvent> getAdminActivity(@PathVariable Long id) {
        DonationEvent a = eventService.getById(id);
        if (a == null) return JsonResult.error(404, "活动不存在");
        return JsonResult.success(a);
    }

    @PostMapping("/admin/activities/{id:\\d+}/status")
    public JsonResult<Void> updateAdminActivityStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String status = body.get("status");
        if (status == null || status.trim().isEmpty()) {
            return JsonResult.error(400, "status required");
        }
        final java.util.Set<String> ALLOWED = java.util.Set.of("DRAFT","PENDING","PUBLISHED","ONGOING","FINISHED");
        if (!ALLOWED.contains(status)) {
            return JsonResult.error(400, "invalid status value: " + status);
        }
        DonationEvent de = eventService.getById(id);
        if (de == null) return JsonResult.error(404, "活动不存在");
        de.setStatus(status);
        eventService.update(de);
        return JsonResult.success(null);
    }
}