package com.example.xianzhiyun.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.example.xianzhiyun.entity.DonationFeedback;
import com.example.xianzhiyun.mapper.DonationFeedbackMapper;
import com.example.xianzhiyun.utils.JsonResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class DonationFeedbackController {

    @Autowired
    private DonationFeedbackMapper feedbackMapper;

    @GetMapping("/donation_feedback")
    public JsonResult<Map<String, Object>> list(@RequestParam(value = "itemId", required = false) Long itemId,
                                                @RequestParam(value = "limit", required = false) Integer limit) {
        Map<String, Object> params = new HashMap<>();
        params.put("itemId", itemId);
        if (limit != null) params.put("limit", limit);
        List<?> items = feedbackMapper.selectList(params);
        Map<String, Object> resp = new HashMap<>();
        resp.put("items", items);
        return JsonResult.success(resp);
    }

    @PostMapping("/donation_feedback")
    public JsonResult<Map<String, Long>> create(@RequestBody DonationFeedback dto) {
        feedbackMapper.insert(dto);
        Map<String, Long> data = new HashMap<>();
        data.put("id", dto.getId());
        return JsonResult.success(data);
    }

    @DeleteMapping("/donation_feedback/{id:\\d+}")
    public JsonResult<Void> delete(@PathVariable Long id) {
        feedbackMapper.deleteById(id);
        return JsonResult.success(null);
    }
}