package com.example.xianzhiyun.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.example.xianzhiyun.entity.DonationItem;
import com.example.xianzhiyun.service.DonationService;
import com.example.xianzhiyun.utils.JsonResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class DonationController {

    @Autowired
    private DonationService donationService;

    @GetMapping("/donations")
    public JsonResult<Map<String, Object>> list(@RequestParam(value = "eventId", required = false) Long eventId,
                                                @RequestParam(value = "status", required = false) String status,
                                                @RequestParam(value = "keyword", required = false) String keyword,
                                                @RequestParam(value = "page", defaultValue = "1") int page,
                                                @RequestParam(value = "pageSize", defaultValue = "8") int pageSize) {
        if (page < 1) page = 1;
        if (pageSize <= 0) pageSize = 8;
        int offset = (page - 1) * pageSize;

        Map<String, Object> params = new HashMap<>();
        params.put("eventId", eventId);
        params.put("status", status);
        params.put("keyword", keyword);
        params.put("offset", offset);
        params.put("limit", pageSize);

        List<DonationItem> list = donationService.list(params);
        int total = donationService.count(params);

        Map<String, Object> resp = new HashMap<>();
        resp.put("items", list);
        resp.put("total", total);
        resp.put("page", page);
        resp.put("pageSize", pageSize);
        return JsonResult.success(resp);
    }

    @GetMapping("/donations/{id:\\d+}")
    public JsonResult<DonationItem> get(@PathVariable Long id) {
        DonationItem item = donationService.getById(id);
        if (item == null) return JsonResult.error(404, "捐赠物品不存在");
        return JsonResult.success(item);
    }

    @PostMapping("/donations")
    public JsonResult<Map<String, Long>> create(@RequestBody DonationItem dto,
                                                @RequestAttribute("userId") Long userId) {
        dto.setDonorId(userId);
        Long id = donationService.create(dto);
        Map<String, Long> data = new HashMap<>();
        data.put("id", id);
        return JsonResult.success(data);
    }

    @PutMapping("/donations/{id:\\d+}")
    public JsonResult<Void> update(@PathVariable Long id, @RequestBody DonationItem dto) {
        if (dto == null) return JsonResult.error(400, "参数错误");
        dto.setId(id);
        donationService.update(dto);
        return JsonResult.success(null);
    }

    @DeleteMapping("/donations/{id:\\d+}")
    public JsonResult<Void> delete(@PathVariable Long id) {
        donationService.delete(id);
        return JsonResult.success(null);
    }
}