package com.example.xianzhiyun.controller;

import com.example.xianzhiyun.dto.HomeDTO;
import com.example.xianzhiyun.service.HomeService;
import com.example.xianzhiyun.utils.JsonResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api") // 注意：这里是 /api，而不是 /api/home
public class HomeController {

    private final HomeService homeService;

    public HomeController(HomeService homeService) {
        this.homeService = homeService;
    }

    /**
     * GET /api/home
     * 返回首页所需的活动 + 推荐商品（不包含轮播图）
     */
    @GetMapping("/home") // 【核心修改】：这里是 /home
    public JsonResult<HomeDTO> getHome() {
        HomeDTO data = homeService.getHomeData();
        return JsonResult.success(data);
    }
}