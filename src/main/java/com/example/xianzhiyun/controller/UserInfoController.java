package com.example.xianzhiyun.controller;

import com.example.xianzhiyun.entity.UserInfo;
import com.example.xianzhiyun.service.UserInfoService;
import com.example.xianzhiyun.service.FavoriteService;
import com.example.xianzhiyun.utils.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户信息相关接口
 *
 * 修复说明：
 * 已将 @RequestHeader("X-User-Id") 替换为 @RequestAttribute("userId")
 * 直接接收 JwtFilter 解析后的用户ID。
 */
@RestController
@RequestMapping("/api")
public class UserInfoController {

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private FavoriteService favoriteService;

    /**
     * GET /api/user/{id} - 获取用户公开信息
     */
    @GetMapping("/user/{id:\\d+}")
    public JsonResult<Map<String, Object>> getUser(@PathVariable Long id) {
        UserInfo u = userInfoService.getById(id);
        if (u == null) {
            return JsonResult.error(404, "用户未找到");
        }
        Map<String, Object> m = new HashMap<>();
        m.put("id", u.getId());
        m.put("nickname", u.getNickname());
        m.put("avatarUrl", u.getAvatarUrl());
        m.put("creditScore", u.getCreditScore());
        m.put("phone", u.getPhone());
        return JsonResult.success(m);
    }

    /**
     * GET /api/user/profile - 获取当前登录用户资料
     *
     * 修改：使用 @RequestAttribute("userId") 获取 JwtFilter 解析的 ID
     */
    @GetMapping("/user/profile")
    public JsonResult<UserInfo> getProfile(@RequestAttribute("userId") Long userId) {
        UserInfo user = userInfoService.getById(userId);
        if (user == null) {
            return JsonResult.error(404, "用户不存在");
        }
        return JsonResult.success(user);
    }

    /**
     * PUT /api/user/profile - 更新当前登录用户资料
     */
    @PutMapping("/user/profile")
    public JsonResult<?> updateProfile(@RequestAttribute("userId") Long userId,
                                       @RequestBody Map<String, Object> body) {
        if (body == null || body.isEmpty()) {
            return JsonResult.error(400, "无更新内容");
        }
        String nickname = body.get("nickname") != null ? String.valueOf(body.get("nickname")).trim() : null;
        String phone = body.get("phone") != null ? String.valueOf(body.get("phone")).trim() : null;
        String email = body.get("email") != null ? String.valueOf(body.get("email")).trim() : null;
        String avatarUrl = body.get("avatarUrl") != null ? String.valueOf(body.get("avatarUrl")).trim() : null;
        if (phone != null && phone.length() > 64) {
            return JsonResult.error(400, "手机号长度不合法");
        }
        if (email != null && email.length() > 128) {
            return JsonResult.error(400, "邮箱长度不合法");
        }
        if (nickname != null && nickname.length() > 64) {
            return JsonResult.error(400, "昵称长度不合法");
        }
        try {
            UserInfo u = new UserInfo();
            u.setId(userId);
            if (nickname != null) u.setNickname(nickname);
            if (phone != null) u.setPhone(phone);
            if (email != null) u.setEmail(email);
            if (avatarUrl != null) u.setAvatarUrl(avatarUrl);
            userInfoService.updateProfile(u);
            UserInfo updated = userInfoService.getById(userId);
            return JsonResult.success(updated);
        } catch (Exception ex) {
            ex.printStackTrace();
            return JsonResult.error(500, "更新失败");
        }
    }

    /**
     * GET /api/user/points
     * 逻辑调整：如果 URL 传了 userId 参数，则查该用户的；否则查当前登录用户的。
     */
    @GetMapping("/user/points")
    public JsonResult<Map<String, Object>> getUserPoints(
            @RequestAttribute("userId") Long currentUserId,
            @RequestParam(value = "userId", required = false) Long queryUserId) {

        // 优先使用查询参数中的 userId，如果没有，则使用当前登录用户的 ID
        Long targetId = (queryUserId != null) ? queryUserId : currentUserId;

        UserInfo u = userInfoService.getById(targetId);
        if (u == null) {
            return JsonResult.error(404, "用户未找到");
        }
        Map<String, Object> m = new HashMap<>();
        m.put("id", u.getId());
        m.put("points", u.getCreditScore());
        return JsonResult.success(m);
    }

    /**
     * GET /api/user/activities/count
     */
    @GetMapping("/user/activities/count")
    public JsonResult<Map<String, Object>> getActivitiesCount(
            @RequestAttribute("userId") Long userId) {

        // 移除 userActivityService 的调用，改为使用 DonationService 提供的统计（如果你接入）
        long cnt = 0L;
        // cnt = donationService.countDistinctEventsByUserId(userId);

        return JsonResult.success(Map.of("count", cnt));
    }
}