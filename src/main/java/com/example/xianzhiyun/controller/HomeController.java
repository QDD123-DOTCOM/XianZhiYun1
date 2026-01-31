// src/main/java/com/example/xianzhiyun/controller/HomeController.java
package com.example.xianzhiyun.controller;

import com.example.xianzhiyun.dto.HomeDTO;
import com.example.xianzhiyun.service.HomeService;
import com.example.xianzhiyun.utils.JsonResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/home")
public class HomeController {

    private final HomeService homeService;

    public HomeController(HomeService homeService) {
        this.homeService = homeService;
    }

    /**
     * GET /api/home
     * 返回首页所需的活动 + 推荐商品（不包含轮播图）
     */
    @GetMapping
    public JsonResult<HomeDTO> getHome() {
        HomeDTO data = homeService.getHomeData();
        return JsonResult.success(data);
    }
}