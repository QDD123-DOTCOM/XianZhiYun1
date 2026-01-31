package com.example.xianzhiyun.controller;

import com.example.xianzhiyun.service.FavoriteService;
import com.example.xianzhiyun.utils.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user/favorites")
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    /**
     * GET /api/user/favorites/count
     * 修复：使用 @RequestAttribute("userId")
     */
    @GetMapping("/count")
    public JsonResult<Map<String, Long>> count(@RequestAttribute("userId") Long userId) {
        long cnt = favoriteService.countByUserId(userId);
        return JsonResult.success(Map.of("count", cnt));
    }

    /**
     * GET /api/user/favorites?page=&pageSize=
     * 修复：使用 @RequestAttribute("userId")
     */
    @GetMapping
    public JsonResult<Map<String, Object>> list(
            @RequestAttribute("userId") Long userId,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", defaultValue = "20") Integer pageSize) {

        if (page < 1) page = 1;
        if (pageSize < 1) pageSize = 20;

        Map<String, Object> pageData = favoriteService.pageFavorites(userId, page, pageSize);
        return JsonResult.success(pageData);
    }

    /**
     * POST /api/user/favorites/{goodsId} - 添加收藏
     * 修复：使用 @RequestAttribute("userId")
     */
    @PostMapping("/{goodsId}")
    public JsonResult<Map<String, Boolean>> add(@PathVariable Long goodsId,
                                                @RequestAttribute("userId") Long userId) {
        favoriteService.addFavorite(userId, goodsId);
        return JsonResult.success(Map.of("added", true));
    }

    /**
     * DELETE /api/user/favorites/{goodsId} - 取消收藏
     * 修复：使用 @RequestAttribute("userId")
     */
    @DeleteMapping("/{goodsId}")
    public JsonResult<Map<String, Boolean>> remove(@PathVariable Long goodsId,
                                                   @RequestAttribute("userId") Long userId) {
        favoriteService.removeFavorite(userId, goodsId);
        return JsonResult.success(Map.of("removed", true));
    }

    /**
     * GET /api/user/favorites/check?goodsId=xxx - 检查是否已收藏
     * 修复：使用 @RequestAttribute("userId")
     */
    @GetMapping("/check")
    public JsonResult<Map<String, Boolean>> check(@RequestParam Long goodsId,
                                                  @RequestAttribute("userId") Long userId) {
        boolean fav = favoriteService.isFavorited(userId, goodsId);
        return JsonResult.success(Map.of("favorited", fav));
    }
}