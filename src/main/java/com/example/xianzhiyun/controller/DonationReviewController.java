package com.example.xianzhiyun.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.example.xianzhiyun.entity.DonationReview;
import com.example.xianzhiyun.service.ReviewService;
import com.example.xianzhiyun.utils.JsonResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class DonationReviewController {

    @Autowired
    private ReviewService reviewService;

    @GetMapping("/donation_review")
    public JsonResult<List<DonationReview>> listByItem(@RequestParam(value = "itemId", required = false) Long itemId) {
        if (itemId == null) return JsonResult.error(400, "缺少 itemId");
        List<DonationReview> list = reviewService.listByItemId(itemId);
        return JsonResult.success(list);
    }

    @PostMapping("/donation_review")
    public JsonResult<Map<String, Long>> create(@RequestBody DonationReview dto) {
        Long id = reviewService.create(dto);
        Map<String, Long> data = new HashMap<>();
        data.put("id", id);
        return JsonResult.success(data);
    }

    @DeleteMapping("/donation_review/{id:\\d+}")
    public JsonResult<Void> delete(@PathVariable Long id) {
        reviewService.delete(id);
        return JsonResult.success(null);
    }
}