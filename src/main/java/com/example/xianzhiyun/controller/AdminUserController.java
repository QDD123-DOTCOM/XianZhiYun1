package com.example.xianzhiyun.controller;

import com.example.xianzhiyun.entity.UserInfo;
import com.example.xianzhiyun.service.UserInfoService;
import com.example.xianzhiyun.utils.JsonResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminUserController {

    private final UserInfoService userInfoService;

    public AdminUserController(UserInfoService userInfoService) {
        this.userInfoService = userInfoService;
    }

    /**
     * GET /api/admin/users
     * 参数：page（默认1）, size（默认10）, keyword（可选，匹配openid或nickname）
     * 返回：JsonResult.success({ total, list })
     */
    @GetMapping("/users")
    public JsonResult<Map<String, Object>> listUsers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {

        // 假设 UserInfoService 提供 pageUsers 方法，否则用自定义实现
        // 如果没有分页实现，我们先做简单的全部查询并手动分页
        List<UserInfo> all = userInfoService.findByKeyword(keyword); // 请在 service 实现
        int total = all.size();
        int from = Math.max(0, (page - 1) * size);
        int to = Math.min(total, from + size);
        List<UserInfo> pageList = all.subList(from, to);

        Map<String, Object> data = new HashMap<>();
        data.put("total", total);
        data.put("list", pageList);
        return JsonResult.success(data);
    }
}